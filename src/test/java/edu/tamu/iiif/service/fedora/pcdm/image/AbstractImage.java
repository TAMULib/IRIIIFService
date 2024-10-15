package edu.tamu.iiif.service.fedora.pcdm.image;

import edu.tamu.iiif.service.fedora.pcdm.AbstractFedoraPcdm;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmImageManifestService;
import javax.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public abstract class AbstractImage extends AbstractFedoraPcdm {

    protected Resource image;

    @PostConstruct
    private void constructResoruces() {
        image = new ClassPathResource(getMockFilePath(JSON_DIR, "image.json"));
    }

    /**
     * Get the manifest service.
     *
     * @return The manifest service.
     */
    abstract protected FedoraPcdmImageManifestService getManifestService();

}
