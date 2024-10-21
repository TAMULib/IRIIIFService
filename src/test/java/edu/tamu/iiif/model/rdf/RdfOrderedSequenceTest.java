package edu.tamu.iiif.model.rdf;

import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.tamu.iiif.utility.RdfModelUtility;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RdfOrderedSequenceTest {

    @Test
    public void testRdfOrderedSequence() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/collection_container.rdf"), "UTF-8");
        Model model = createRdfModel(rdf);
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

    /**
     * Provide the behavior from the original RdfModelUtility createRdfModel().
     *
     * The original createRdfModel() from RdfModelUtility is removed.
     * This is added so that the test continue to operate with minimal changes.
     * It is likely a good idea to change this behavior in the future.
     *
     * @param rdf
     * @return
     * @throws IOException
     */
    private static Model createRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

}
