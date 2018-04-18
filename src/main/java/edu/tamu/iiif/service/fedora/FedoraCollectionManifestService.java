package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_MEMBER_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.COLLECTION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Collection;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.references.CollectionReference;
import de.digitalcollections.iiif.presentation.model.api.v2.references.ManifestReference;
import de.digitalcollections.iiif.presentation.model.impl.v2.CollectionImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.ManifestReferenceImpl;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfOrderedSequence;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraCollectionManifestService extends AbstractFedoraManifestService {

    @Override
    protected String generateManifest(String handle) throws URISyntaxException, IOException {
        return mapper.writeValueAsString(generateCollection(handle));
    }

    private Collection generateCollection(String path) throws URISyntaxException, IOException {
        RdfResource rdfResource = getRdfResource(path);

        URI id = buildId(path);

        PropertyValueSimpleImpl label = getTitle(rdfResource);

        List<Metadata> metadata = getDublinCoreMetadata(rdfResource);

        Collection collection = new CollectionImpl(id, label, metadata);

        collection.setSubCollections(getSubcollections(rdfResource));

        collection.setManifests(getResourceManifests(rdfResource));

        collection.setDescription(getDescription(rdfResource));

        collection.setLogo(getLogo(rdfResource));

        collection.setViewingHint("multi-part");

        return collection;
    }

    private List<CollectionReference> getSubcollections(RdfResource rdfResource) throws URISyntaxException {
        List<CollectionReference> subcollections = new ArrayList<CollectionReference>();
        return subcollections;
    }

    private List<ManifestReference> getResourceManifests(RdfResource rdfResource) throws URISyntaxException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        List<String> members = getMembers(rdfResource);
        for (String id : members) {
            PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
            manifests.add(new ManifestReferenceImpl(getFedoraIIIFPresentationUri(id), label));
        }
        return manifests;
    }

    private List<String> getMembers(RdfResource rdfResource) {

        List<String> members = new ArrayList<String>();

        Optional<String> firstId = getIdByPredicate(rdfResource.getModel(), IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = getIdByPredicate(rdfResource.getModel(), IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = rdfResource.getModel().getResource(firstId.get());
                getOrderedMembers(new RdfOrderedSequence(rdfResource.getModel(), firstResource, firstId.get(), lastId.get()), members);
            }
        }

        if (members.isEmpty()) {

            Resource resource = rdfResource.getResource();
            Property property = rdfResource.getProperty(PCDM_HAS_MEMBER_PREDICATE);
            if (resource != null && property != null) {
                NodeIterator nodes = rdfResource.getModel().listObjectsOfProperty(resource, property);
                while (nodes.hasNext()) {
                    members.add(nodes.nextNode().toString());
                }
            }
        }
        return members;
    }

    private void getOrderedMembers(RdfOrderedSequence fedoraRdfOrderedSequence, List<String> members) {

        Model model = getRdfModel(fedoraRdfOrderedSequence.getResource().getURI());

        Optional<String> id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {

            if (!fedoraRdfOrderedSequence.isLast()) {

                members.add(id.get());

                Optional<String> nextId = getIdByPredicate(model, IANA_NEXT_PREDICATE);

                if (nextId.isPresent()) {
                    Resource resource = fedoraRdfOrderedSequence.getModel().getResource(nextId.get());
                    fedoraRdfOrderedSequence.setResource(resource);
                    fedoraRdfOrderedSequence.setCurrentId(nextId.get());
                    getOrderedMembers(fedoraRdfOrderedSequence, members);
                }

            }

        }
    }

    @Override
    protected ManifestType getManifestType() {
        return COLLECTION;
    }

}
