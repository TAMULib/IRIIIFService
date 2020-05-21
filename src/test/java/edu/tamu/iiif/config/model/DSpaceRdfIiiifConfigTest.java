package edu.tamu.iiif.config.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DSpaceRdfIiiifConfigTest {

    @Test
    public void testDSpaceRdfIiifConfig() {
        DSpaceRdfIiifConfig config = new DSpaceRdfIiifConfig();

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

        config.setLabelPredicates(labelPredicates);
        config.setDescriptionPredicates(descriptionPredicates);
        config.setAttributionPredicates(attributionPredicates);
        config.setLicensePrecedence(licensePrecedence);
        config.setMetadataPrefixes(metadataPrefixes);
        config.setMetadataExclusion(metadataExclusion);
        config.setUrl("http://localhost:9000/fcrepo/rest");
        config.setIdentifier("fedora-pcdm");
        config.setContextAsMetadata(true);

        assertEquals(4, config.getLabelPredicates().size());
        assertEquals("http://purl.org/dc/elements/1.1/title", config.getLabelPredicates().get(0));
        assertEquals("http://purl.org/dc/terms/title", config.getLabelPredicates().get(1));
        assertEquals("http://www.w3.org/2000/01/rdf-schema#label", config.getLabelPredicates().get(2));
        assertEquals("http://purl.org/dc/elements/1.1/identifier", config.getLabelPredicates().get(3));

        assertEquals(3, config.getDescriptionPredicates().size());
        assertEquals("http://purl.org/dc/terms/abstract", config.getDescriptionPredicates().get(0));
        assertEquals("http://purl.org/dc/terms/description", config.getDescriptionPredicates().get(1));
        assertEquals("http://purl.org/dc/elements/1.1/description", config.getDescriptionPredicates().get(2));

        assertEquals(8, config.getAttributionPredicates().size());
        assertEquals("http://purl.org/dc/elements/1.1/creator", config.getAttributionPredicates().get(0));
        assertEquals("http://purl.org/dc/terms/creator", config.getAttributionPredicates().get(1));
        assertEquals("http://purl.org/dc/elements/1.1/contributor", config.getAttributionPredicates().get(2));
        assertEquals("http://purl.org/dc/terms/contributor", config.getAttributionPredicates().get(3));
        assertEquals("http://purl.org/dc/elements/1.1/publisher", config.getAttributionPredicates().get(4));
        assertEquals("http://purl.org/dc/terms/publisher", config.getAttributionPredicates().get(5));
        assertEquals("http://purl.org/dc/elements/1.1/rights", config.getAttributionPredicates().get(6));
        assertEquals("http://purl.org/dc/terms/rightsHolder", config.getAttributionPredicates().get(7));

        assertEquals(1, config.getLicensePrecedence().size());
        assertEquals("http://purl.org/dc/terms/license", config.getLicensePrecedence().get(0));

        assertEquals(2, config.getMetadataPrefixes().size());
        assertEquals("http://purl.org/dc/elements/1.1/", config.getMetadataPrefixes().get(0));
        assertEquals("http://purl.org/dc/terms/", config.getMetadataPrefixes().get(1));

        assertEquals(2, config.getMetadataExclusion().size());
        assertEquals("http://purl.org/dc/terms/description", config.getMetadataExclusion().get(0));
        assertEquals("http://purl.org/dc/elements/1.1/description", config.getMetadataExclusion().get(1));

        assertEquals("http://localhost:9000/fcrepo/rest", config.getUrl());
        assertEquals("fedora-pcdm", config.getIdentifier());
        assertEquals(true, config.getContextAsMetadata());
    }

}
