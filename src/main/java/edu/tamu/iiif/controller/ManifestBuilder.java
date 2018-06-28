package edu.tamu.iiif.controller;

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

    public static ManifestBuilder build(HttpServletResponse response, ManifestRequest request) {
        return new ManifestBuilder(response, request);
    }

}
