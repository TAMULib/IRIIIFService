package edu.tamu.iiif.service.fedora.pcdm;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;

import edu.tamu.iiif.config.model.FedoraPcdmIiifConfig;
import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractFedoraPcdmManifestServiceTest extends AbstractManifestServiceTest {

    @MockBean
    private FedoraPcdmIiifConfig config;

    protected static final String FEDORA_URL = "http://localhost:9000/fcrepo/rest";

    protected static final String FEDORA_PCDM_IDENTIFIER = "fedora-pcdm";

    protected void setup(AbstractFedoraPcdmManifestService fedoraPcdmManifestService) {
        super.setup(fedoraPcdmManifestService);
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

        when(config.getUrl()).thenReturn(FEDORA_URL);
        when(config.getIdentifier()).thenReturn(FEDORA_PCDM_IDENTIFIER);
        when(config.getContextAsMetadata()).thenReturn(true);
    }

    @Override
    protected String getRepoRdfIdentifier() {
        return FEDORA_PCDM_IDENTIFIER;
    }

    @Override
    protected String getRepoBaseUrl() {
        return FEDORA_URL + "/";
    }

}
