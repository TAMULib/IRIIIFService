package edu.tamu.iiif.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.iiif.service.ManifestService;
import edu.tamu.iiif.utility.StringUtility;

public abstract class AbstractManifestController<S extends ManifestService> {

    @Autowired
    private S manifestService;

    protected void sendManifest(HttpServletResponse response, String path, Boolean update) throws IOException, URISyntaxException {
        setResponseFile(response, getFileName(path));
        sendJsonFile(response, manifestService.getManifest(path, update));
    }

    private void setResponseFile(HttpServletResponse response, String filename) {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }

    private String getFileName(String path) {
        return StringUtility.encode(path) + ".json";
    }

    private void sendJsonFile(HttpServletResponse response, String json) throws IOException {
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        IOUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
    }

}
