package edu.tamu.iiif.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class NotFoundExceptionTest {

    @Test(expected = NotFoundException.class)
    public void testThrowNotFoundException() throws NotFoundException {
        throw new NotFoundException("This is only a test!");
    }

}
