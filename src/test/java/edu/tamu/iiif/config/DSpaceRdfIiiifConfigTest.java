package edu.tamu.iiif.config;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.config.model.DSpaceRdfIiifConfig;

@RunWith(SpringRunner.class)
public class DSpaceRdfIiiifConfigTest {

    @Test
    public void testDSpaceRdfIiifConfig() {
        DSpaceRdfIiifConfig config = new DSpaceRdfIiifConfig();

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

        config.setLabelPrecedence(labelPrecedence);
        config.setDescriptionPrecedence(descriptionPrecedence);
        config.setMetadataPrefixes(metadataPrefixes);
        config.setUrl("http://localhost:8080");
        config.setIdentifier("dspace-rdf");
        config.setWebapp("xmlui");

        assertEquals(4, config.getLabelPrecedence().size());
        assertEquals("http://purl.org/dc/elements/1.1/title", config.getLabelPrecedence().get(0));
        assertEquals("http://purl.org/dc/terms/title", config.getLabelPrecedence().get(1));
        assertEquals("http://www.w3.org/2000/01/rdf-schema#label", config.getLabelPrecedence().get(2));
        assertEquals("http://purl.org/dc/elements/1.1/identifier", config.getLabelPrecedence().get(3));
        assertEquals(3, config.getDescriptionPrecedence().size());
        assertEquals("http://purl.org/dc/terms/abstract", config.getDescriptionPrecedence().get(0));
        assertEquals("http://purl.org/dc/terms/description", config.getDescriptionPrecedence().get(1));
        assertEquals("http://purl.org/dc/elements/1.1/description", config.getDescriptionPrecedence().get(2));
        assertEquals(2, config.getMetadataPrefixes().size());
        assertEquals("http://purl.org/dc/elements/1.1/", config.getMetadataPrefixes().get(0));
        assertEquals("http://purl.org/dc/terms/", config.getMetadataPrefixes().get(1));

        assertEquals("http://localhost:8080", config.getUrl());
        assertEquals("dspace-rdf", config.getIdentifier());
        assertEquals("xmlui", config.getWebapp());
    }

}
