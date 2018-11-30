package edu.tamu.iiif.model;

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
        this.url = url;
    }
    
    public RedisResource(String id, String url) {
        this(url);
        this.id = id;
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
