package edu.tamu.iiif.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.rdf.RdfResource;

public class RdfModelUtility {

    public static Model createRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

    public static Optional<String> getObject(Model model, String uri) {
        Optional<String> id = Optional.empty();
        NodeIterator firstNodeItr = model.listObjectsOfProperty(model.getProperty(uri));
        if (firstNodeItr.hasNext()) {
            id = Optional.of(firstNodeItr.next().toString());
        }
        return id;
    }

    public static Optional<String> getObject(RdfResource rdfResource, String uri) {
        return getObject(rdfResource.getModel(), uri);
    }

    public static String getParameterizedId(String uri, ManifestRequest request) {
        if (!request.getAllowed().isEmpty()) {
            uri += "?allow=" + request.getAllowed();
        } else if (!request.getDisallowed().isEmpty()) {
            uri += "?disallow=" + request.getDisallowed();
        }
        return uri;
    }

    public static String getParameterizedId(ManifestRequest request) {
        return getParameterizedId(request.getContext(), request);
    }

}
