package edu.tamu.iiif.controller;

import java.util.ArrayList;
import java.util.List;

public class ManifestRequest {

    private final String context;

    private final boolean update;

    private final List<String> allowed;

    private final List<String> disallowed;

    public ManifestRequest(String context, boolean update, List<String> allowed, List<String> disallowed) {
        this.context = context;
        this.update = update;
        this.allowed = allowed;
        this.disallowed = disallowed;
    }

    public String getContext() {
        return context;
    }

    public boolean isUpdate() {
        return update;
    }

    public String getAllowed() {
        return String.join(",", allowed);
    }

    public String getDisallowed() {
        return String.join(",", disallowed);
    }

    public static ManifestRequest of(String path, boolean update, List<String> allowed, List<String> disallowed) {
        return new ManifestRequest(path, update, allowed, disallowed);
    }

    public static ManifestRequest of(String path, boolean update) {
        return new ManifestRequest(path, update, new ArrayList<String>(), new ArrayList<String>());
    }

}
