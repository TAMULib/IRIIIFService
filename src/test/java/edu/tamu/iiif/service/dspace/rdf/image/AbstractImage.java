package edu.tamu.iiif.service.dspace.rdf.image;

import edu.tamu.iiif.service.dspace.rdf.AbstractDspaceRdf;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfImageManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractImage extends AbstractDspaceRdf {

    protected Resource image;

    @PostConstruct
    private void constructResources() {
        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected DSpaceRdfImageManifestService getManifestService();

}
