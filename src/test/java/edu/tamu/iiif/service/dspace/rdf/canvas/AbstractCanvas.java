package edu.tamu.iiif.service.dspace.rdf.canvas;

import edu.tamu.iiif.service.dspace.rdf.AbstractDspaceRdf;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCanvasManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractCanvas extends AbstractDspaceRdf {

    protected Resource itemRdf;

    protected Resource image;
    protected Resource canvas;

    @PostConstruct
    private void constructResources() {
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item.rdf"));

        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
        canvas = new ClassPathResource(getMockFilePath(JSON_DIR, "canvas.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected DSpaceRdfCanvasManifestService getManifestService();

}
