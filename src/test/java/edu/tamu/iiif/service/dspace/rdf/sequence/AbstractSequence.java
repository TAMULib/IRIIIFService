package edu.tamu.iiif.service.dspace.rdf.sequence;

import edu.tamu.iiif.service.dspace.rdf.AbstractDspaceRdf;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfSequenceManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractSequence extends AbstractDspaceRdf {

    protected Resource itemRdf;

    protected Resource image;
    protected Resource sequence;

    @PostConstruct
    private void constructResources() {
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item.rdf"));

        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
        sequence = new ClassPathResource(getMockFilePath(JSON_DIR, "sequence.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected DSpaceRdfSequenceManifestService getManifestService();

}
