package edu.tamu.iiif.model.repo;

import org.springframework.data.repository.CrudRepository;

import edu.tamu.iiif.model.RedisResource;

public interface RedisResourceRepo extends CrudRepository<RedisResource, String> {

    public boolean existsByUrl(String url);

    public RedisResource findByUrl(String url);

}
