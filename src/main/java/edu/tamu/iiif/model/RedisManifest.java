package edu.tamu.iiif.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("manifest")
public class RedisManifest {

    @Id
    private String id;

    @Indexed
    private String path;

    @Indexed
    private ManifestType type;

    @Indexed
    private RepositoryType repository;

    private String json;

    private final Long creation;

    public RedisManifest() {
        this.creation = new Date().getTime();
    }

    public RedisManifest(String path, ManifestType type, RepositoryType repository, String json) {
        this();
        this.path = path;
        this.type = type;
        this.repository = repository;
        this.json = json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ManifestType getType() {
        return type;
    }

    public void setType(ManifestType type) {
        this.type = type;
    }

    public RepositoryType getRepository() {
        return repository;
    }

    public void setRepository(RepositoryType repository) {
        this.repository = repository;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Long getCreation() {
        return creation;
    }

}
