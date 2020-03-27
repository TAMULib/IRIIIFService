package edu.tamu.iiif.service;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@Service
public class RedisResourceResolver implements ResourceResolver {

    private final static UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" }, UrlValidator.ALLOW_LOCAL_URLS);

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    public String lookup(String url) throws InvalidUrlException, NotFoundException {
        if (!urlValidator.isValid(url)) {
            throw new InvalidUrlException(String.format("%s is not a valid URL!", url));
        }
        if (redisResourceRepo.existsByUrl(url)) {
            return redisResourceRepo.findByUrl(url).getId();    
        }
        throw new NotFoundException(String.format("Resource with url %s not found!", url));
    }

    public String create(String url) throws InvalidUrlException {
        if (!urlValidator.isValid(url)) {
            throw new InvalidUrlException(String.format("%s is not a valid URL!", url));
        }
        return redisResourceRepo.save(new RedisResource(url)).getId();
    }

    public String resolve(String id) throws NotFoundException {
        if (redisResourceRepo.exists(id)) {
            return redisResourceRepo.findOne(id).getUrl();    
        }
        throw new NotFoundException(String.format("Resource with id %s not found!", id));
    }

    public void remove(String id) throws NotFoundException {
        if (redisResourceRepo.exists(id)) {
            redisResourceRepo.delete(id);
        }
        throw new NotFoundException(String.format("Resource with id %s not found!", id));
    }

}
