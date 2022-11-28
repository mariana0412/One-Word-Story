package entities;

import exceptions.EntityException;
import exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Test;
import usecases.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseTest {

    @Test
    public void testResultCodeIsCorrectForGivenException() {
        String message = "My super message";
        Response response = Response.fromException(new PlayerNotFoundException(message), message);
        assertEquals(response.getCode(), Response.ResCode.PLAYER_NOT_FOUND);
        assertEquals(response.getMessage(), message);
    }

    @Test
    public void testResultCodeFailIsReturnedWhenExceptionIsIncorrect() {
        String message = "My super message";
        EntityException myStrangeException = new EntityException("Rickroll");
        Response response = Response.fromException(myStrangeException, message);
        assertEquals(response.getCode(), Response.ResCode.FAIL);
        assertEquals(response.getMessage(), message);
    }

}
