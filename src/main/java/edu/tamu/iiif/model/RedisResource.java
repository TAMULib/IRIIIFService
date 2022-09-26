package edu.tamu.iiif.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("resource")
public class RedisResource {

    @Id
    private String id;

    @Indexed
    private String url;

    public RedisResource(String url) {
        this.id = UUID.nameUUIDFromBytes(url.getBytes()).toString();
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
