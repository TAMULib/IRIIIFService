package edu.tamu.iiif.model.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.custom.RedisResourceRepoCustom;

public interface RedisResourceRepo extends CrudRepository<RedisResource, String>, RedisResourceRepoCustom {

    public Optional<RedisResource> findByUrl(String url);

}
