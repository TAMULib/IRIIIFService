package edu.tamu.iiif.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import edu.tamu.iiif.model.rdf.RdfResource;

public class RdfModelUtility {

    public static Model createRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

    public static Optional<String> getIdByPredicate(Model model, String predicate) {
        Optional<String> id = Optional.empty();
        NodeIterator firstNodeItr = model.listObjectsOfProperty(model.getProperty(predicate));
        while (firstNodeItr.hasNext()) {
            id = Optional.of(firstNodeItr.next().toString());
        }
        return id;
    }

    public static Optional<String> getIdByPredicate(RdfResource rdfResource, String predicate) {
        Optional<String> id = Optional.empty();
        NodeIterator firstNodeItr = rdfResource.getModel().listObjectsOfProperty(rdfResource.getResource(), rdfResource.getModel().getProperty(predicate));
        while (firstNodeItr.hasNext()) {
            id = Optional.of(firstNodeItr.next().toString());
        }
        return id;
    }

    public static Optional<String> getObject(RdfResource rdfResource, String uri) {
        Optional<String> metadatum = Optional.empty();
        Optional<Statement> statement = Optional.ofNullable(rdfResource.getStatementOfPropertyWithId(uri));
        if (statement.isPresent()) {
            RDFNode object = statement.get().getObject();
            if (!object.toString().isEmpty()) {
                metadatum = Optional.of(object.toString());
            }
        }
        return metadatum;
    }

}
