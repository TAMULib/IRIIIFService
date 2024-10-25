package edu.tamu.iiif.service.dspace.rdf.collection;

import edu.tamu.iiif.service.dspace.rdf.AbstractDspaceRdf;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCollectionManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractCollection extends AbstractDspaceRdf {

    protected Resource collectionRdf;
    protected Resource subcommunityRdf;
    protected Resource communityRdf;
    protected Resource itemRdf;

    protected Resource collection;
    protected Resource collections;

    @PostConstruct
    private void constructResources() {
        collectionRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "collection.rdf"));
        subcommunityRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "subcommunity.rdf"));
        communityRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "community.rdf"));
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item.rdf"));

        collection = new ClassPathResource(getMockFilePath(JSON_DIR, "collection.json"));
        collections = new ClassPathResource(getMockFilePath(JSON_DIR, "collections.json"));
    }

    /**
     * Get the manifest path for a collection.
     *
     * @return Path to the manifest.
     */
    abstract protected String getManifestCollectionPath();

    /**
     * Get the manifest path for community 1.
     *
     * @return Path to the manifest.
     */
    abstract protected String getManifestCommunity1Path() ;

    /**
     * Get the manifest path for community 2.
     *
     * @return Path to the manifest.
     */
    abstract protected String getManifestCommunity2Path();

    /**
     * Get the manifest path for subcommunity.
     *
     * @return Path to the manifest.
     */
    abstract protected String getManifestSubcommunityPath();

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected DSpaceRdfCollectionManifestService getManifestService();

}
