package edu.tamu.iiif.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.rdf.RdfResource;

public class RdfModelUtility {

    public static Model createRdfModel(String rdf) throws IOException {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        System.out.print("\n\n\nDEBUG: stream returned is " + rdf + "\n\n\n");
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

    public static boolean hasObject(Model model, String uri) {
        NodeIterator firstNodeItr = model.listObjectsOfProperty(model.getProperty(uri));
        if (firstNodeItr.hasNext()) {
            return true;
        }
        return false;
    }

    public static Optional<String> findObject(Model model, String uri) {
        NodeIterator firstNodeItr = model.listObjectsOfProperty(model.getProperty(uri));
        if (firstNodeItr.hasNext()) {
            return Optional.of(firstNodeItr.next().toString());
        }
        return Optional.empty();
    }

    public static Optional<String> findObject(RdfResource rdfResource, String uri) {
        return findObject(rdfResource.getModel(), uri);
    }

    public static List<String> getObjects(Model model, String uri) {
        List<String> values = new ArrayList<String>();
        NodeIterator firstNodeItr = model.listObjectsOfProperty(model.getProperty(uri));
        while (firstNodeItr.hasNext()) {
            values.add(firstNodeItr.next().toString());
        }
        return values;
    }

    public static List<String> getObjects(RdfResource rdfResource, String uri) {
        return getObjects(rdfResource.getModel(), uri);
    }

    public static List<String> getObjects(RdfResource rdfResource, List<String> uris) {
        return uris.stream()
            .map(uri -> getObjects(rdfResource, uri))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public static String getParameterizedId(final String id, ManifestRequest request) {
        String parameterizedId = id;
        if (!request.getAllowed().isEmpty()) {
            parameterizedId += "?allow=" + request.getAllowed();
        } else if (!request.getDisallowed().isEmpty()) {
            parameterizedId += "?disallow=" + request.getDisallowed();
        }
        return parameterizedId;
    }

    public static String getParameterizedId(ManifestRequest request) {
        return getParameterizedId(request.getContext(), request);
    }

}
