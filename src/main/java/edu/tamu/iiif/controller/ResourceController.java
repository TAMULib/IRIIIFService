package edu.tamu.iiif.controller;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    @GetMapping(produces = "application/json")
    public List<RedisResource> getResources() {
        List<RedisResource> resources = new ArrayList<RedisResource>();
        redisResourceRepo.findAll().forEach(resources::add);
        return resources;
    }

    @GetMapping(value = "/{id}", produces = "text/plain")
    public String getResourceUrl(@PathVariable String id) throws NotFoundException {
        Optional<RedisResource> redisFileResolver = Optional.ofNullable(redisResourceRepo.findOne(id));
        if (redisFileResolver.isPresent()) {
            return redisFileResolver.get().getUrl();
        }
        throw new NotFoundException(String.format("Unable to resolve resource with id %s", id));
    }

    @GetMapping(value = "/{id}/redirect")
    public RedirectView redirectToResource(@PathVariable String id) throws IOException, NotFoundException {
        Optional<RedisResource> redisFileResolver = Optional.ofNullable(redisResourceRepo.findOne(id));
        if (redisFileResolver.isPresent()) {
            RedirectView redirect = new RedirectView(redisFileResolver.get().getUrl());
            redirect.setStatusCode(MOVED_PERMANENTLY);
            return redirect;
        }
        throw new NotFoundException(String.format("Unable to resolve resource with id %s", id));

    }

    @GetMapping(value = "/lookup", produces = "text/plain")
    public String getResourceId(@RequestParam(value = "uri", required = true) String uri) throws NotFoundException {
        Optional<RedisResource> redisFileResolver = redisResourceRepo.findByUrl(uri);
        if (redisFileResolver.isPresent()) {
            return redisFileResolver.get().getId();
        }
        throw new NotFoundException(String.format("No resourse found with uri %s", uri));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = { POST, PUT }, produces = "text/plain")
    public String addResource(@RequestParam(value = "uri", required = true) String uri) throws InvalidUrlException {
        return redisResourceRepo.getOrCreate(uri).getId();
    }

    @DeleteMapping(value = "/{id}", produces = "text/plain")
    public String removeResource(@PathVariable String id) throws NotFoundException {
        Optional<RedisResource> redisFileResolver = Optional.ofNullable(redisResourceRepo.findOne(id));
        if (redisFileResolver.isPresent()) {
            redisResourceRepo.delete(redisFileResolver.get());
            return "Success";
        }
        throw new NotFoundException(String.format("Unable to resolve resource with id %s", id));
    }

}
