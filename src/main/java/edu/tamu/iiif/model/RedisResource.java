package edu.tamu.iiif.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("resource")
public class RedisResource {

    @Id
    private final String id;

    @Indexed
    private final String url;

    public RedisResource(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

}
