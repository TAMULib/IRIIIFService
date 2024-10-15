package edu.tamu.iiif.service.fedora.pcdm.sequence;

import edu.tamu.iiif.service.fedora.pcdm.AbstractFedoraPcdm;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmSequenceManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractSequence extends AbstractFedoraPcdm {

    protected Resource itemRdf;
    protected Resource itemFilesRdf;
    protected Resource itemFilesEntryRdf;

    protected Resource image;
    protected Resource sequence;

    @PostConstruct
    private void constructResoruces() {
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container.rdf"));
        itemFilesRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container_files.rdf"));
        itemFilesEntryRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container_files_entry.rdf"));

        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
        sequence = new ClassPathResource(getMockFilePath(JSON_DIR, "sequence.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected FedoraPcdmSequenceManifestService getManifestService();

}
