package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.rdf.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.PCDM_HAS_MEMBER_PREDICATE;
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
import de.digitalcollections.iiif.presentation.model.api.v2.references.ManifestReference;
import de.digitalcollections.iiif.presentation.model.impl.v2.CollectionImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.ManifestReferenceImpl;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.fedora.FedoraRdfOrderedSequence;
import edu.tamu.iiif.model.rdf.fedora.FedoraRdfResource;

@Service
public class FedoraCollectionManifestService extends AbstractFedoraManifestService {

    @Override
    protected String generateManifest(String handle) throws URISyntaxException, IOException {
        return mapper.writeValueAsString(generateCollection(handle));
    }

    private Collection generateCollection(String path) throws URISyntaxException, IOException {
        FedoraRdfResource fedoraRdfResource = getFedoraRdfResource(path);

        URI id = buildId(path);

        PropertyValueSimpleImpl label = getTitle(fedoraRdfResource);

        List<Metadata> metadata = getDublinCoreMetadata(fedoraRdfResource);

        Collection collection = new CollectionImpl(id, label, metadata);

        collection.setManifests(getResourceManifests(fedoraRdfResource));

        collection.setDescription(getDescription(fedoraRdfResource));

        collection.setLogo(getLogo(fedoraRdfResource));

        collection.setViewingHint("multi-part");

        return collection;
    }

    private List<ManifestReference> getResourceManifests(FedoraRdfResource fedoraRdfResource) throws URISyntaxException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        List<String> members = getMembers(fedoraRdfResource);
        for (String id : members) {
            PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
            manifests.add(new ManifestReferenceImpl(getFedoraIIIFPresentationUrl(id), label));
        }
        return manifests;
    }

    private List<String> getMembers(FedoraRdfResource fedoraRdfResource) {

        List<String> members = new ArrayList<String>();

        Optional<String> firstId = getIdByPredicate(fedoraRdfResource.getModel(), IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = getIdByPredicate(fedoraRdfResource.getModel(), IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = fedoraRdfResource.getModel().getResource(firstId.get());
                getOrderedMembers(new FedoraRdfOrderedSequence(fedoraRdfResource.getModel(), firstResource, firstId.get(), lastId.get()), members);
            }
        }

        if (members.isEmpty()) {

            Resource resource = fedoraRdfResource.getResource();
            Property property = fedoraRdfResource.getProperty(PCDM_HAS_MEMBER_PREDICATE);
            if (resource != null && property != null) {
                NodeIterator nodes = fedoraRdfResource.getModel().listObjectsOfProperty(resource, property);
                while (nodes.hasNext()) {
                    members.add(nodes.nextNode().toString());
                }
            }
        }
        return members;
    }

    private void getOrderedMembers(FedoraRdfOrderedSequence fedoraRdfOrderedSequence, List<String> members) {

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
