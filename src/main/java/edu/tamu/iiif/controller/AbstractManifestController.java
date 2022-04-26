package edu.tamu.iiif.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.iiif.service.ManifestService;

public abstract class AbstractManifestController<S extends ManifestService> {

    @Autowired
    private S manifestService;

    public abstract void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException;

    protected void sendManifest(ManifestBuilder builder) throws IOException, URISyntaxException {
        setResponseFile(builder.getResponse());
        sendJsonFile(builder.getResponse(), manifestService.getManifest(builder.getRequest()));
    }

    private void setResponseFile(HttpServletResponse response) {
        response.setContentType("application/json");
    }

    private void sendJsonFile(HttpServletResponse response, String json) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(StringEscapeUtils.unescapeJson(json).getBytes(StandardCharsets.UTF_8));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

}
