package edu.tamu.iiif.config.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iiif.fedora")
public class FedoraPcdmIiifConfig extends AbstractIiifConfig {

    public FedoraPcdmIiifConfig() {
        super();
        setUrl("http://localhost:9000/fcrepo/rest");
        setIdentifier("fedora");
    }

}
