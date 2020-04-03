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
        when(config.getUrl()).thenReturn(FEDORA_URL);
        when(config.getIdentifier()).thenReturn(FEDORA_PCDM_IDENTIFIER);
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
