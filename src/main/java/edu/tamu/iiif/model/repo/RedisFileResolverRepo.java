package edu.tamu.iiif.model.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.tamu.iiif.model.RedisFileResolver;

public interface RedisFileResolverRepo extends CrudRepository<RedisFileResolver, String> {

    public Optional<RedisFileResolver> findById(String id);

    public Optional<RedisFileResolver> findByUrl(String url);

}
