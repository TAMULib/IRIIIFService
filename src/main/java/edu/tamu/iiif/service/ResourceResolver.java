package edu.tamu.iiif.service;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.exception.NotFoundException;

public interface ResourceResolver {

    public String lookup(String url) throws InvalidUrlException, NotFoundException;

    public String create(String url) throws InvalidUrlException;

    public String resolve(String id) throws NotFoundException;

    public void remove(String id) throws NotFoundException;

}
