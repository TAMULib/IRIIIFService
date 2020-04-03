package edu.tamu.iiif.service;

import java.net.URISyntaxException;

import edu.tamu.iiif.exception.NotFoundException;

public interface ResourceResolver {

    public String lookup(String url) throws URISyntaxException, NotFoundException;

    public String create(String url) throws URISyntaxException;

    public String resolve(String id) throws NotFoundException;

    public void remove(String id) throws NotFoundException;

}
