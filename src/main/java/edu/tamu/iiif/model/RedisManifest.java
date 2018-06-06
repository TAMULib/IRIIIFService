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
    private final String path;

    @Indexed
    private final ManifestType type;

    @Indexed
    private final RepositoryType repository;

    @Indexed
    private final String allowed;

    @Indexed
    private final String disallowed;

    private String json;

    private final Long creation;

    public RedisManifest(String path, ManifestType type, RepositoryType repository, String json) {
        this.creation = new Date().getTime();
        this.path = path;
        this.type = type;
        this.repository = repository;
        this.json = json;
        this.allowed = "";
        this.disallowed = "";
    }

    public RedisManifest(String path, ManifestType type, RepositoryType repository, String allowed, String disallowed, String json) {
        this.creation = new Date().getTime();
        this.path = path;
        this.type = type;
        this.repository = repository;
        this.json = json;
        this.allowed = allowed;
        this.disallowed = disallowed;
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

    public ManifestType getType() {
        return type;
    }

    public RepositoryType getRepository() {
        return repository;
    }

    public String getAllowed() {
        return allowed;
    }

    public String getDisallowed() {
        return disallowed;
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
