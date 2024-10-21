package edu.tamu.iiif.service.dspace.rdf.presentation;

import edu.tamu.iiif.service.dspace.rdf.AbstractDspaceRdf;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfPresentationManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractPresentation extends AbstractDspaceRdf {

    protected Resource collectionRdf;
    protected Resource communityRdf;
    protected Resource itemRdf;
    protected Resource subcommunityRdf;

    protected Resource collectionPresentation;
    protected Resource image;
    protected Resource presentation;
    protected Resource presentationAllow;
    protected Resource presentationDisallow;
    protected Resource sequence;

    @PostConstruct
    private void constructResources() {
        collectionRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "collection.rdf"));
        communityRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "community.rdf"));
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item.rdf"));
        subcommunityRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "subcommunity.rdf"));

        collectionPresentation = new ClassPathResource(getMockFilePath(JSON_DIR, "collection_presentation.json"));
        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
        presentation = new ClassPathResource(getMockFilePath(JSON_DIR, "presentation.json"));
        presentationAllow = new ClassPathResource(getMockFilePath(JSON_DIR, "presentation_allow.json"));
        presentationDisallow = new ClassPathResource(getMockFilePath(JSON_DIR, "presentation_disallow.json"));
        sequence = new ClassPathResource(getMockFilePath(JSON_DIR, "sequence.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected DSpaceRdfPresentationManifestService getManifestService();

    /**
     * Get the manifest path for a collection.
     *
     * @return Path to the manifest.
     */
    abstract protected String getManifestCollectionPath();

}
