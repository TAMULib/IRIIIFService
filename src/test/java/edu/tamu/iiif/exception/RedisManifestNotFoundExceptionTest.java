package edu.tamu.iiif.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RedisManifestNotFoundExceptionTest {

    @Test
    public void testThrowRedisManifestNotFoundException() {
        Assertions.assertThrows(RedisManifestNotFoundException.class, () -> {
            throw new RedisManifestNotFoundException("This is only a test!");
        });
    }

}
