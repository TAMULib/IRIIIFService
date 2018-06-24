package edu.tamu.iiif.service.dspace.rdf;

import static org.springframework.test.util.ReflectionTestUtils.setField;

import edu.tamu.iiif.service.AbstractManifestServiceTest;
import edu.tamu.iiif.service.dspace.rdf.AbstractDSpaceRdfManifestService;

public abstract class AbstractDSpaceRdfManifestServiceTest extends AbstractManifestServiceTest {

    protected static final String DSPACE_URL = "http://localhost:8080";

    protected static final String DSPACE_WEBAPP = "xmlui";

    protected static final String DSPACE_RDF_IDENTIFIER = "dspace-rdf";

    protected void setup(AbstractDSpaceRdfManifestService dspaceRdfManifestService) {
        super.setup(dspaceRdfManifestService);
        setField(dspaceRdfManifestService, "dspaceUrl", DSPACE_URL);
        setField(dspaceRdfManifestService, "dspaceWebapp", DSPACE_WEBAPP);
        setField(dspaceRdfManifestService, "dspaceRdfIdentifier", DSPACE_RDF_IDENTIFIER);
    }

}
