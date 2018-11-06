package edu.tamu.iiif.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisFileResolver;
import edu.tamu.iiif.model.repo.RedisFileResolverRepo;

@RestController
@RequestMapping("/url")
public class FileResolverController {

    @Autowired
    private RedisFileResolverRepo redisFileResolverRepo;

    @GetMapping("/{id}")
    public String getFileUrl(@PathVariable String id) throws NotFoundException {
        Optional<RedisFileResolver> redisFileResolver = redisFileResolverRepo.findById(id);
        if (redisFileResolver.isPresent()) {
            return redisFileResolver.get().getUrl();
        }
        throw new NotFoundException(String.format("Unable to resolve file with id %s", id));
    }

}
