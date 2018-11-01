package edu.tamu.iiif.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.iiif.service.ManifestService;
import edu.tamu.iiif.utility.StringUtility;

public abstract class AbstractManifestController<S extends ManifestService> {

    @Autowired
    private S manifestService;

    public abstract void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException;

    protected void sendManifest(ManifestBuilder builder) throws IOException, URISyntaxException {
        setResponseFile(builder.getResponse(), getFileName(builder.getRequest().getContext()));
        sendJsonFile(builder.getResponse(), manifestService.getManifest(builder.getRequest()));
    }

    private void setResponseFile(HttpServletResponse response, String filename) {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }

    private String getFileName(String path) {
        return StringUtility.encode(path) + ".json";
    }

    private void sendJsonFile(HttpServletResponse response, String json) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(StringEscapeUtils.unescapeJson(json).getBytes(StandardCharsets.UTF_8));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

}
