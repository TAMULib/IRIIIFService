package edu.tamu.iiif.service.dspace;

import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractDSpaceManifestServiceTest extends AbstractManifestServiceTest {

    protected static final String DSPACE_URL = "http://localhost:8080";

    protected static final String DSPACE_WEBAPP = "xmlui";

    protected static final String DSPACE_RDF_IDENTIFIER = "dspace-rdf";

    protected void setup(AbstractDSpaceManifestService dspaceManifestService) {
        super.setup(dspaceManifestService);
        ReflectionTestUtils.setField(dspaceManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dspaceManifestService, "dspaceWebapp", DSPACE_WEBAPP);
        ReflectionTestUtils.setField(dspaceManifestService, "dspaceRdfIdentifier", DSPACE_RDF_IDENTIFIER);
    }

}
