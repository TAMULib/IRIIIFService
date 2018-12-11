package edu.tamu.iiif.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class InvalidUrlExceptionTest {

    @Test(expected = InvalidUrlException.class)
    public void testThrowInvalidUrlException() throws InvalidUrlException {
        throw new InvalidUrlException("This is only a test!");
    }

}
