package edu.tamu.iiif.model.repo.custom;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.model.RedisResource;

public interface RedisResourceRepoCustom {

    public RedisResource getOrCreate(String url) throws InvalidUrlException;

}
