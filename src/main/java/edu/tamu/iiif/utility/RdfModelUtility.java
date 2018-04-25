package edu.tamu.iiif.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RdfModelUtility {

    public static Model createRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

}
