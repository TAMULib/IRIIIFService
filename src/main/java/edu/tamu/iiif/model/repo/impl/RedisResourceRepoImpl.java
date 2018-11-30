package edu.tamu.iiif.model.repo.impl;

import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;
import edu.tamu.iiif.model.repo.custom.RedisResourceRepoCustom;

public class RedisResourceRepoImpl implements RedisResourceRepoCustom {

    UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    @Override
    public RedisResource getOrCreate(String url) throws InvalidUrlException {
        if (!urlValidator.isValid(url)) {
            throw new InvalidUrlException(String.format("%s is not a valid URL!", url));
        }
        Optional<RedisResource> redisFileResolver = redisResourceRepo.findByUrl(url);
        if (!redisFileResolver.isPresent()) {
            redisFileResolver = Optional.of(redisResourceRepo.save(new RedisResource(url)));
        }
        return redisFileResolver.get();
    }

}
