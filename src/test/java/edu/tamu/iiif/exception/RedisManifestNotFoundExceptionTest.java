package edu.tamu.iiif.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RedisManifestNotFoundExceptionTest {

    @Test(expected = RedisManifestNotFoundException.class)
    public void testThrowRedisManifestNotFoundException() throws RedisManifestNotFoundException {
        throw new RedisManifestNotFoundException("This is only a test!");
    }

}
