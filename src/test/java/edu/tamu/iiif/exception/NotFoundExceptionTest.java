package edu.tamu.iiif.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class NotFoundExceptionTest {

    @Test
    public void testThrowNotFoundException() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException("This is only a test!");
        });
    }

}
