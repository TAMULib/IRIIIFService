package edu.tamu.iiif.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
public abstract class AbstractManifestControllerTest implements ManifestControllerTest {

    @Autowired
    protected MockMvc mockMvc;

}
