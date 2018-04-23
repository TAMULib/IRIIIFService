package edu.tamu.iiif;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IrIiifService.class)
public class IrIiifServiceTest {

    @Test
    public void testContext() {
        Assert.assertTrue(true);
    }

}
