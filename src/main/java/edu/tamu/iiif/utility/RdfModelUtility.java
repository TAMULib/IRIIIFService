package edu.tamu.iiif.utility;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.rdf.RdfResource;
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
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdfModelUtility {

    private final static Logger logger = LoggerFactory.getLogger(RdfModelUtility.class);

    public static Model createRdfModel(String rdf) throws IOException {
        // THIS IS VERY BAD PRACTICE BUT THIS MAY HELP PROVIDE US SOME TEST DATA.
        //String decodedRDFDataBecauseJenaIsSilly = rdf.replaceAll("%20", " ");

        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        System.out.print("\n\n\nDEBUG: stream returned (updated) is " + rdf + "\n\n\n");
        /*RDFParser parser = RDFParser.create()
            .source(rdf)
            .lang(RDFLanguages.TURTLE)
            .errorHandler(ErrorHandlerFactory.errorHandlerStd(logger))
            .build()
            ;*/

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
