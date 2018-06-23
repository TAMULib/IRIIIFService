package edu.tamu.iiif.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class ManifestBuilder {

    private final HttpServletResponse response;

    private final ManifestRequest request;

    public ManifestBuilder(HttpServletResponse response, ManifestRequest request) {
        this.response = response;
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ManifestRequest getRequest() {
        return request;
    }

    public static ManifestBuilder build(HttpServletResponse response, String path, boolean update, List<String> allowed, List<String> disallowed) {
        return new ManifestBuilder(response, ManifestRequest.of(path, update, allowed, disallowed));
    }

}
