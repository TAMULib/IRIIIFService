package edu.tamu.iiif.config.model;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import edu.tamu.iiif.config.model.AdminConfig.Credentials;

@Component
@ConfigurationProperties(prefix = "iiif.resolver")
public class ResolverConfig {

    public ResolverType type;

    public String url;

    public Credentials credentials;

    public ResolverType getType() {
        return type;
    }

    public void setType(ResolverType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public boolean hasCredentials() {
        return credentials != null && StringUtils.isNotEmpty(credentials.getUsername()) && StringUtils.isNotEmpty(credentials.getPassword());
    }

    public String getBase64Credentials() {
        return Base64.getEncoder().encodeToString(String.format("%s:%s", credentials.getUsername(), credentials.getPassword()).getBytes());
    }

    public static enum ResolverType {
        REDIS, REMOTE
    }

}
