package usecases.submitWord;
import usecases.Response;

/**
 * The data that the ViewModel uses to update the GUI if the word is determined to be valid.
 */
public class SwOutputDataValidWord {
    /**
     * Word that was submitted, represented as a string.
     */
    private String word;

    /**
     * ID of the player that attempted to submit.
     */
    private String playerId;

    /**
     * Response.
     */
    private Response response;

    /**
     * Constructor.
     * @param word New word.
     * @param playerId Player ID.
     * @param response Result Code.
     */
    public SwOutputDataValidWord(String word, String playerId, Response response) {
        this.word = word;
        this.playerId = playerId;
        this.response = response;
    }
}
