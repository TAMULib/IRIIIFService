package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.FEDORA_IDENTIFIER;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RepositoryTypeTest {

    @Test
    public void testRepositoryType() {
        Assert.assertEquals(DSPACE_IDENTIFIER, RepositoryType.DSPACE.getName());
        Assert.assertEquals(FEDORA_IDENTIFIER, RepositoryType.FEDORA.getName());
    }

}
