package edu.tamu.iiif.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    @GetMapping
    public List<RedisResource> getResources() {
        List<RedisResource> resources = new ArrayList<RedisResource>();
        redisResourceRepo.findAll().forEach(resources::add);
        return resources;
    }

    @GetMapping("/{id}")
    public String getResourceUrl(@PathVariable String id) throws NotFoundException {
        Optional<RedisResource> redisFileResolver = Optional.ofNullable(redisResourceRepo.findOne(id));
        if (redisFileResolver.isPresent()) {
            return redisFileResolver.get().getUrl();
        }
        throw new NotFoundException(String.format("Unable to resolve resource with id %s", id));
    }

    @GetMapping("/lookup")
    public String getResourceId(@RequestParam(value = "url", required = true) String url) throws NotFoundException {
        Optional<RedisResource> redisFileResolver = redisResourceRepo.findByUrl(url);
        if (redisFileResolver.isPresent()) {
            return redisFileResolver.get().getId();
        }
        throw new NotFoundException(String.format("No resourse found with url %s", url));
    }

    @RequestMapping(method = { POST, PUT })
    public String putResource(@RequestParam(value = "url", required = true) String url) throws InvalidUrlException {
        return redisResourceRepo.getOrCreate(url).getId();
    }

    @DeleteMapping("/{id}")
    public String removeResource(@PathVariable String id) throws NotFoundException {
        Optional<RedisResource> redisFileResolver = Optional.ofNullable(redisResourceRepo.findOne(id));
        if (redisFileResolver.isPresent()) {
            redisResourceRepo.delete(redisFileResolver.get());
            return "Sucess";
        }
        throw new NotFoundException(String.format("Unable to resolve resource with id %s", id));
    }

}
