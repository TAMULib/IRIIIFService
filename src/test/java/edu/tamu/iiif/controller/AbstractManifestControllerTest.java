package edu.tamu.iiif.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public abstract class AbstractManifestControllerTest implements ManifestControllerTest {

    @Autowired
    protected MockMvc mockMvc;

}
