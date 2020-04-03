package edu.tamu.iiif.config.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iiif.dspace")
public class DSpaceRdfIiifConfig extends AbstractIiifConfig {

    private String webapp;

    public DSpaceRdfIiifConfig() {
        super();
        setUrl("http://localhost:8080");
        setIdentifier("dspace");
        setWebapp("xmlui");
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

}
