package edu.tamu.iiif.model.rdf;

import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.iiif.utility.RdfModelUtility;

@ExtendWith(SpringExtension.class)
public class RdfOrderedSequenceTest {

    @Test
    public void testRdfOrderedSequence() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/collection_container.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        System.out.println(model);
        Resource resource = model.getResource("http://localhost:9000/fcrepo/rest/mwbObjects/TGWCatalog");
        RdfResource rdfResource = new RdfResource(model, resource);
        Optional<String> firstId = RdfModelUtility.findObject(rdfResource.getModel(), IANA_FIRST_PREDICATE);
        Optional<String> lastId = RdfModelUtility.findObject(rdfResource.getModel(), IANA_LAST_PREDICATE);

        System.out.println(firstId.get());
        System.out.println(lastId.get());

        RdfOrderedResource rdfOrderedSequence = new RdfOrderedResource(model, resource, firstId.get(), lastId.get());
        assertNotNull(rdfOrderedSequence);
        assertEquals(model, rdfOrderedSequence.getModel());

        assertEquals(firstId.get(), rdfOrderedSequence.getFirstId());
        assertEquals(lastId.get(), rdfOrderedSequence.getLastId());

        assertTrue(rdfOrderedSequence.isFirst());
        assertFalse(rdfOrderedSequence.isLast());

        rdfOrderedSequence.setCurrentId(rdfOrderedSequence.getLastId());

        assertTrue(rdfOrderedSequence.isLast());
        assertFalse(rdfOrderedSequence.isFirst());

        assertEquals(lastId.get(), rdfOrderedSequence.getCurrentId());
    }

}
