package edu.tamu.iiif.service.dspace.rdf;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;

import edu.tamu.iiif.config.model.DSpaceRdfIiifConfig;
import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractDSpaceRdfManifestServiceTest extends AbstractManifestServiceTest {

    @MockBean
    private DSpaceRdfIiifConfig config;

    protected static final String DSPACE_URL = "http://localhost:8080";

    protected static final String DSPACE_RDF_IDENTIFIER = "dspace-rdf";

    protected static final String DSPACE_WEBAPP = "xmlui";

    protected void setup(AbstractDSpaceRdfManifestService dspaceRdfManifestService) {
        super.setup(dspaceRdfManifestService);
        List<String> labelPredicates = new ArrayList<String>();
        labelPredicates.add("http://purl.org/dc/elements/1.1/title");
        labelPredicates.add("http://purl.org/dc/terms/title");
        labelPredicates.add("http://www.w3.org/2000/01/rdf-schema#label");
        labelPredicates.add("http://purl.org/dc/elements/1.1/identifier");

        List<String> descriptionPredicates = new ArrayList<String>();
        descriptionPredicates.add("http://purl.org/dc/terms/abstract");
        descriptionPredicates.add("http://purl.org/dc/terms/description");
        descriptionPredicates.add("http://purl.org/dc/elements/1.1/description");

        List<String> attributionPredicates = new ArrayList<String>();
        attributionPredicates.add("http://purl.org/dc/elements/1.1/creator");
        attributionPredicates.add("http://purl.org/dc/terms/creator");
        attributionPredicates.add("http://purl.org/dc/elements/1.1/contributor");
        attributionPredicates.add("http://purl.org/dc/terms/contributor");
        attributionPredicates.add("http://purl.org/dc/elements/1.1/publisher");
        attributionPredicates.add("http://purl.org/dc/terms/publisher");
        attributionPredicates.add("http://purl.org/dc/elements/1.1/rights");
        attributionPredicates.add("http://purl.org/dc/terms/rightsHolder");

        List<String> licensePrecedence = new ArrayList<String>();
        licensePrecedence.add("http://purl.org/dc/terms/license");

        List<String> metadataPrefixes = new ArrayList<String>();
        metadataPrefixes.add("http://purl.org/dc/elements/1.1/");
        metadataPrefixes.add("http://purl.org/dc/terms/");

        List<String> metadataExclusion = new ArrayList<String>();
        metadataExclusion.add("http://purl.org/dc/terms/description");
        metadataExclusion.add("http://purl.org/dc/elements/1.1/description");

        when(config.getLabelPredicates()).thenReturn(labelPredicates);
        when(config.getDescriptionPredicates()).thenReturn(descriptionPredicates);
        when(config.getAttributionPredicates()).thenReturn(attributionPredicates);
        when(config.getLicensePrecedence()).thenReturn(licensePrecedence);
        when(config.getMetadataPrefixes()).thenReturn(metadataPrefixes);
        when(config.getMetadataExclusion()).thenReturn(metadataExclusion);

        when(config.getUrl()).thenReturn(DSPACE_URL);
        when(config.getIdentifier()).thenReturn(DSPACE_RDF_IDENTIFIER);
        when(config.getWebapp()).thenReturn(DSPACE_WEBAPP);
        when(config.getContextAsMetadata()).thenReturn(true);
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
