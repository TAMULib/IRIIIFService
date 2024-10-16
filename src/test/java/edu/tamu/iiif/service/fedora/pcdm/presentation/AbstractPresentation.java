package edu.tamu.iiif.service.fedora.pcdm.presentation;

import edu.tamu.iiif.service.fedora.pcdm.AbstractFedoraPcdm;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmPresentationManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractPresentation extends AbstractFedoraPcdm {

    protected Resource itemRdf;
    protected Resource itemFilesRdf;
    protected Resource itemFilesEntryRdf;

    protected Resource image;
    protected Resource presentation;
    protected Resource presentationAllow;
    protected Resource presentationDisallow;

    @PostConstruct
    private void constructResources() {
        itemRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container.rdf"));
        itemFilesRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container_files.rdf"));
        itemFilesEntryRdf = new ClassPathResource(getMockFilePath(RDF_DIR, "item_container_files_entry.rdf"));

        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
        presentation = new ClassPathResource(getMockFilePath(JSON_DIR, "presentation.json"));
        presentationAllow = new ClassPathResource(getMockFilePath(JSON_DIR, "presentation_allow.json"));
        presentationDisallow = new ClassPathResource(getMockFilePath(JSON_DIR, "presentation_disallow.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected FedoraPcdmPresentationManifestService getManifestService();

}
