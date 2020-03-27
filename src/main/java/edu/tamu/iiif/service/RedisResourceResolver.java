package edu.tamu.iiif.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@Service
public class RedisResourceResolver implements ResourceResolver {

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    public String lookup(String url) throws NotFoundException {
        if (redisResourceRepo.existsByUrl(url)) {
            return redisResourceRepo.findByUrl(url).getId();    
        }
        throw new NotFoundException(String.format("Resource with url %s not found!", url));
    }

    public String create(String url) {
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
        } else {
            throw new NotFoundException(String.format("Resource with id %s not found!", id));
        }
    }

}
