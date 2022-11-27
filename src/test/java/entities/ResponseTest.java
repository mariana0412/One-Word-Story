package entities;

import exceptions.EntityException;
import exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usecases.Response;

public class ResponseTest {

    @Test
    public void testResultCodeIsCorrectForGivenException() {
        String message = "My super message";
        Response response = Response.fromException(new PlayerNotFoundException(message), message);
        Assertions.assertEquals(response.getCode(), Response.ResCode.PLAYER_NOT_FOUND);
        Assertions.assertEquals(response.getMessage(), message);
    }

    @Test
    public void testResultCodeFailIsReturnedWhenExceptionIsIncorrect() {
        String message = "My super message";
        EntityException myStrangeException = new EntityException("Rickroll");
        Response response = Response.fromException(myStrangeException, message);
        Assertions.assertEquals(response.getCode(), Response.ResCode.FAIL);
        Assertions.assertEquals(response.getMessage(), message);
    }

}
