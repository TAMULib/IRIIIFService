package edu.tamu.iiif.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import edu.tamu.iiif.annotation.ContextIdentifier;
import edu.tamu.iiif.service.ManifestService;
import edu.tamu.iiif.utility.StringUtility;

public abstract class AbstractManifestController<S extends ManifestService> {

    @Autowired
    private S manifestService;

    // @formatter:off
    public abstract void manifest(    
        HttpServletResponse response,
        @ContextIdentifier String path,
        @RequestParam(value = "update", required = false, defaultValue = "false") boolean update,
        @RequestParam(value = "allow", required = false, defaultValue = "") List<String> allowed,
        @RequestParam(value = "disallow", required = false, defaultValue = "") List<String> disallowed        
    ) throws IOException, URISyntaxException;
    // @formatter:on

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
        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

}
