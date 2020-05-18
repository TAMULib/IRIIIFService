package edu.tamu.iiif.model.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.tamu.iiif.model.RedisResource;

public interface RedisResourceRepo extends CrudRepository<RedisResource, String> {

    public Optional<RedisResource> findByUrl(String url);

}
