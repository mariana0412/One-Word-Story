package usecases.join_public_lobby;

import entities.DisplayNameChecker;
import entities.LobbyManager;
import entities.Player;
import entities.PlayerFactory;
import entities.games.Game;
import entities.games.GameFactory;
import entities.games.GameFactoryRegular;
import entities.games.GameRegular;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import usecases.Response;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class JoinPublicLobbyTest {

    private final TestOutputBoundary testOutputBoundary = new TestOutputBoundary();
    private final SimpleDisplayNameChecker simpleDisplayNameChecker = new SimpleDisplayNameChecker();
    private final GameFactory gameFactory = new GameFactoryRegular();
    private final PlayerFactory playerFactory = new PlayerFactory(simpleDisplayNameChecker);
    private JplInteractor interactor;

    private static class TestOutputBoundary implements JplOutputBoundary {

        List<JplOutputDataResponse> joinedPoolResponses = new CopyOnWriteArrayList<>();
        List<JplOutputDataJoinedGame> joinedGameResponses = new CopyOnWriteArrayList<>();
        List<JplOutputDataResponse> cancelledResponses = new CopyOnWriteArrayList<>();

        @Override
        public void inPool(JplOutputDataResponse dataJoinedPool) {
            joinedPoolResponses.add(dataJoinedPool);
        }

        @Override
        public void inGame(JplOutputDataJoinedGame dataJoinedGame) {
            joinedGameResponses.add(dataJoinedGame);
        }

        @Override
        public void cancelled(JplOutputDataResponse dataCancelled) {
            cancelledResponses.add(dataCancelled);
        }
    }

    private static class SimpleDisplayNameChecker implements DisplayNameChecker {
        @Override
        public boolean checkValid(String displayName) {
            return true;
        }
    }

    @Before
    public void setupJplInteractor(){
        LobbyManager lobbyManager = new LobbyManager(this.playerFactory, this.gameFactory);
        this.interactor = new JplInteractor(lobbyManager, this.testOutputBoundary);
    }

    /**
     * Test that the response to players being added the pool is properly being registered
     */
    @Test(timeout=10000)
    public void checkAllJoinPoolQueriesRegistered() {
        Player firstPlayer = new Player("First", "1");
        Player secondPlayer = new Player("Second", "2");
        Set<String> expectedIds = new HashSet<>();
        expectedIds.add(firstPlayer.getPlayerId());
        expectedIds.add(secondPlayer.getPlayerId());

        JplInputData firstInputData = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData secondInputData = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());

        // Start threads which will add the players to the pool and call the output
        this.interactor.joinPublicLobby(firstInputData);
        this.interactor.joinPublicLobby(secondInputData);

        // Waits for the above threads to provide output for both players
        // Will timeout if this never occurs, so test will properly fail
        while (this.testOutputBoundary.joinedPoolResponses.size() < 2) {
            Thread.onSpinWait();
        }

        assertEquals(2, this.testOutputBoundary.joinedPoolResponses.size());
        assertEquals(0, this.testOutputBoundary.joinedGameResponses.size());
        assertEquals(0, this.testOutputBoundary.cancelledResponses.size());

        Set<String> actualIds = new HashSet<>();
        for(JplOutputDataResponse response : this.testOutputBoundary.joinedPoolResponses) {
            assertEquals(response.getRes().getCode(), Response.ResCode.SUCCESS);
            actualIds.add(response.getPlayerId());
        }
        assertIterableEquals(expectedIds, actualIds);
    }

    /**
     * Test that JPL responds properly to players joining the game
     */
    @Test(timeout=10000)
    public void checkAllJoinedGame() {
        Player firstPlayer = new Player("Name", "1");
        Player secondPlayer = new Player("Name", "2");
        JplInputData inputDataFirst = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData inputDataSecond = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());
        JplInteractor.JplThread threadFirst = this.interactor.new JplThread(inputDataFirst);
        JplInteractor.JplThread threadSecond = this.interactor.new JplThread(inputDataSecond);

        Queue<Player> initialPlayers = new LinkedList<>();
        initialPlayers.add(firstPlayer);
        initialPlayers.add(secondPlayer);
        Game game = new GameRegular(initialPlayers);

        Assertions.assertThrows(IllegalMonitorStateException.class, () -> threadFirst.onJoinGamePlayer(game));
        Assertions.assertThrows(IllegalMonitorStateException.class, () -> threadSecond.onJoinGamePlayer(game));
        threadFirst.run();
        threadSecond.run();
        assertEquals(2, this.testOutputBoundary.joinedPoolResponses.size());
        assertEquals(2, this.testOutputBoundary.joinedGameResponses.size());
        assertEquals(0, this.testOutputBoundary.cancelledResponses.size());
    }

    /**
     * Test that JPL responds properly to a player cancelling their waiting
     */
    @Test(timeout = 10000)
    public void testCancelWaiting() {
        Player firstPlayer = new Player("Name", "1");
        Player secondPlayer = new Player("Name", "2");
        Player thirdPlayer = new Player("Busy", "3");
        JplInputData inputDataFirst = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData inputDataSecond = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());
        JplInputData inputDataThird = new JplInputData(thirdPlayer.getDisplayName(), thirdPlayer.getPlayerId());
        JplInteractor.JplThread threadFirst = this.interactor.new JplThread(inputDataFirst);
        JplInteractor.JplThread threadSecond = this.interactor.new JplThread(inputDataSecond);
        JplInteractor.JplThread threadThird = this.interactor.new JplThread(inputDataThird);

        Assertions.assertThrows(IllegalMonitorStateException.class, threadThird::onCancelPlayer);
        threadThird.run();

        Queue<Player> initialPlayers = new LinkedList<>();
        initialPlayers.add(firstPlayer);
        initialPlayers.add(secondPlayer);
        Game game = new GameRegular(initialPlayers);

        Assertions.assertThrows(IllegalMonitorStateException.class, () -> threadFirst.onJoinGamePlayer(game));
        Assertions.assertThrows(IllegalMonitorStateException.class, () -> threadSecond.onJoinGamePlayer(game));
        threadFirst.run();
        threadSecond.run();
        assertEquals(3, this.testOutputBoundary.joinedPoolResponses.size());
        assertEquals(2, this.testOutputBoundary.joinedGameResponses.size());
        assertEquals(1, this.testOutputBoundary.cancelledResponses.size());
    }

    /**
     * Test that if a player joins with a duplicate ID, they fail properly
     */
    @Test(timeout=10000)
    public void testDuplicateIds(){
        Player firstPlayer = new Player("Player", "1");
        Player secondPlayer = new Player("player", "1");
        JplInputData inputDataFirst = new JplInputData(firstPlayer.getDisplayName(), firstPlayer.getPlayerId());
        JplInputData inputDataSecond = new JplInputData(secondPlayer.getDisplayName(), secondPlayer.getPlayerId());
        JplInteractor.JplThread threadFirst = this.interactor.new JplThread(inputDataFirst);
        JplInteractor.JplThread threadSecond = this.interactor.new JplThread(inputDataSecond);
        Queue<Player> initialPlayers = new LinkedList<>();
        initialPlayers.add(firstPlayer);
        initialPlayers.add(secondPlayer);
        Game game = new GameRegular(initialPlayers);

        Assertions.assertThrows(IllegalMonitorStateException.class, () -> threadFirst.onJoinGamePlayer(game));
        Assertions.assertThrows(IllegalMonitorStateException.class, () -> threadSecond.onJoinGamePlayer(game));
        threadFirst.run();
        threadSecond.run();
        assertEquals(2, this.testOutputBoundary.joinedPoolResponses.size());
        assertEquals(1, this.testOutputBoundary.joinedGameResponses.size());
        assertEquals(0, this.testOutputBoundary.cancelledResponses.size());
        int expectedFailNumber = 1;
        int actualFailNumber = 0;
        for(JplOutputDataResponse response : this.testOutputBoundary.joinedPoolResponses) {
            if(response.getRes().getCode() == Response.ResCode.ID_IN_USE) {
                actualFailNumber++;
            }
        }
        assertEquals(expectedFailNumber, actualFailNumber);
    }

}
