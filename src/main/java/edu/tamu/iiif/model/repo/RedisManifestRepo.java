package edu.tamu.iiif.model.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;

public interface RedisManifestRepo extends CrudRepository<RedisManifest, String> {

    public Optional<RedisManifest> findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(String path, ManifestType type, String repository, String allowed, String disallowed);

    public List<RedisManifest> findByPath(String contextPath);

}
