package edu.tamu.iiif.service;

import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@Service
@ConditionalOnProperty(value = "iiif.resolver.type", havingValue = "redis", matchIfMissing = true)
public class RedisResourceResolver implements ResourceResolver {

    public final static UrlValidator URL_VALIDATOR = new UrlValidator(new String[] { "http", "https" }, UrlValidator.ALLOW_LOCAL_URLS);

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    public String lookup(String url) throws URISyntaxException, NotFoundException {
        if (!URL_VALIDATOR.isValid(url)) {
            throw new URISyntaxException(url, "Not a valid URL");
        }
        Optional<RedisResource> resource = redisResourceRepo.findByUrl(url);
        if (resource.isPresent()) {
            return resource.get().getId();
        }
        throw new NotFoundException(String.format("Resource with url %s not found!", url));
    }

    public String create(String url) throws URISyntaxException {
        if (!URL_VALIDATOR.isValid(url)) {
            throw new URISyntaxException(url, "Not a valid URL");
        }
        return redisResourceRepo.save(new RedisResource(url)).getId();
    }

    public String resolve(String id) throws NotFoundException {
        if (redisResourceRepo.existsById(id)) {
            return redisResourceRepo.findById(id).get().getUrl();
        }
        throw new NotFoundException(String.format("Resource with id %s not found!", id));
    }

    public void remove(String id) throws NotFoundException {
        if (redisResourceRepo.existsById(id)) {
            redisResourceRepo.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Resource with id %s not found!", id));
        }
    }

}
