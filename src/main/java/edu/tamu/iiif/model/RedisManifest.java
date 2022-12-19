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
    private String repository;

    @Indexed
    private String allowed;

    @Indexed
    private String disallowed;

    private String json;

    private Long creation;

    public RedisManifest() {
        this.creation = new Date().getTime();
        this.allowed = "";
        this.disallowed = "";
    }

    public RedisManifest(String path, ManifestType type, String repository, String json) {
        this();
        this.path = path;
        this.type = type;
        this.repository = repository;
        this.json = json;
    }

    public RedisManifest(String path, ManifestType type, String repository, String allowed, String disallowed, String json) {
        this();
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

    public void setPath(String path) {
        this.path = path;
    }

    public ManifestType getType() {
        return type;
    }

    public void setType(ManifestType type) {
        this.type = type;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getAllowed() {
        return allowed;
    }

    public void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    public String getDisallowed() {
        return disallowed;
    }

    public void setDisallowed(String disallowed) {
        this.disallowed = disallowed;
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

    public void setCreation(Long creation) {
        this.creation = creation;
    }

}
