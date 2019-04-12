package edu.tamu.iiif.service.dspace.rdf;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;

import edu.tamu.iiif.config.DSpaceRdfIiifConfig;
import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractDSpaceRdfManifestServiceTest extends AbstractManifestServiceTest {

    @MockBean
    private DSpaceRdfIiifConfig config;

    protected static final String DSPACE_URL = "http://localhost:8080";

    protected static final String DSPACE_RDF_IDENTIFIER = "dspace-rdf";

    protected static final String DSPACE_WEBAPP = "xmlui";

    protected void setup(AbstractDSpaceRdfManifestService dspaceRdfManifestService) {
        super.setup(dspaceRdfManifestService);
        List<String> labelPrecedence = new ArrayList<String>();
        labelPrecedence.add("http://purl.org/dc/elements/1.1/title");
        labelPrecedence.add("http://purl.org/dc/terms/title");
        labelPrecedence.add("http://www.w3.org/2000/01/rdf-schema#label");
        labelPrecedence.add("http://purl.org/dc/elements/1.1/identifier");
        List<String> descriptionPrecedence = new ArrayList<String>();
        descriptionPrecedence.add("http://purl.org/dc/terms/abstract");
        descriptionPrecedence.add("http://purl.org/dc/terms/description");
        descriptionPrecedence.add("http://purl.org/dc/elements/1.1/description");
        List<String> metadataPrefixes = new ArrayList<String>();
        metadataPrefixes.add("http://purl.org/dc/elements/1.1/");
        metadataPrefixes.add("http://purl.org/dc/terms/");
        when(config.getLabelPrecedence()).thenReturn(labelPrecedence);
        when(config.getDescriptionPrecedence()).thenReturn(descriptionPrecedence);
        when(config.getMetadataPrefixes()).thenReturn(metadataPrefixes);
        when(config.getUrl()).thenReturn(DSPACE_URL);
        when(config.getIdentifier()).thenReturn(DSPACE_RDF_IDENTIFIER);
        when(config.getWebapp()).thenReturn(DSPACE_WEBAPP);
    }

    @Override
    protected String getRepoRdfIdentifier() {
        return DSPACE_RDF_IDENTIFIER;
    }

    @Override
    protected String getRepoBaseUrl() {
        return DSPACE_URL + "/";
    }

}
