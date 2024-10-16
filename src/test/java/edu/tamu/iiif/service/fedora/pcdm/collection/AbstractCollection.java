package edu.tamu.iiif.service.fedora.pcdm.collection;

import edu.tamu.iiif.service.fedora.pcdm.AbstractFedoraPcdm;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCollectionManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractCollection extends AbstractFedoraPcdm {

    protected Resource collectionRdf;
    protected Resource itemRdf;

    protected Resource collection;

    @PostConstruct
    private void constructResources() {
        collectionRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "collection_container.rdf"));
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container.rdf"));

        collection = new ClassPathResource(getMockFilePath(JSON_DIR, "collection.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected FedoraPcdmCollectionManifestService getManifestService();

}
