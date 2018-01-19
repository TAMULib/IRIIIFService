package edu.tamu.iiif.model.repo;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;

public interface RedisManifestRepo extends CrudRepository<RedisManifest, String> {

    Optional<RedisManifest> findByPathAndType(String path, ManifestType type);

}
