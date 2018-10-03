package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.constants.Constants.FEDORA_FCR_METADATA;
import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_COLLECTION;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_FILE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_MEMBER_PREDICATE;
import static edu.tamu.iiif.constants.Constants.W3_TYPE_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.COLLECTION;
import static edu.tamu.iiif.utility.RdfModelUtility.createRdfModel;
import static edu.tamu.iiif.utility.RdfModelUtility.getIdByPredicate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Collection;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.references.CollectionReference;
import de.digitalcollections.iiif.presentation.model.api.v2.references.ManifestReference;
import de.digitalcollections.iiif.presentation.model.impl.v2.CollectionImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.ManifestReferenceImpl;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfOrderedResource;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraPcdmCollectionManifestService extends AbstractFedoraPcdmManifestService {

    @Override
    protected String generateManifest(ManifestRequest request) throws URISyntaxException, IOException {
        String context = request.getContext();
        return mapper.writeValueAsString(generateCollection(request, context));
    }

    private Collection generateCollection(ManifestRequest request, String context) throws URISyntaxException, IOException {

        RdfResource rdfResource = getRdfResource(context);

        URI id = buildId(context);

        PropertyValueSimpleImpl label = getTitle(rdfResource);

        boolean isCollection = isCollection(rdfResource);

        List<Metadata> metadata;

        if (isCollection) {
            // this is the case we are requesting a collection manifest from the collection container
            // lets get the metadata from the objects container
            metadata = getDublinCoreMetadata(new RdfResource(rdfResource, getCollectionObjectsMember(rdfResource)));
        } else {
            metadata = getDublinCoreMetadata(rdfResource);
        }

        Collection collection = new CollectionImpl(id, label, metadata);

        List<CollectionReference> collections = getSubcollections(rdfResource);
        if (!collections.isEmpty()) {
            collection.setSubCollections(collections);
        }

        collection.setManifests(getResourceManifests(request, rdfResource, isCollection));

        collection.setDescription(getDescription(rdfResource));

        collection.setLogo(getLogo(rdfResource));

        collection.setViewingHint("multi-part");

        return collection;
    }

    private boolean isCollection(RdfResource rdfResource) {
        NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(W3_TYPE_PREDICATE);
        while (nodes.hasNext()) {
            RDFNode node = nodes.next();
            if (node.toString().equals(PCDM_COLLECTION)) {
                return true;
            }
        }
        return false;
    }

    private String getCollectionObjectsMember(RdfResource rdfResource) {
        NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);
        if (nodes.hasNext()) {
            RDFNode node = nodes.next();
            return node.toString();
        }
        throw new RuntimeException("Collection does not contain its expected member!");
    }

    private List<CollectionReference> getSubcollections(RdfResource rdfResource) throws URISyntaxException {
        List<CollectionReference> subcollections = new ArrayList<CollectionReference>();

        return subcollections;
    }

    private List<ManifestReference> getResourceManifests(ManifestRequest request, RdfResource rdfResource, boolean isCollection) throws URISyntaxException, IOException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();

        NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);

        if (isCollection) {
            while (nodes.hasNext()) {
                RDFNode node = nodes.next();
                manifests.addAll(gatherResourceManifests(request, new RdfResource(rdfResource, node.toString())));
            }
        } else {
            while (nodes.hasNext()) {
                RDFNode node = nodes.next();
                String id = node.toString();
                PropertyValueSimpleImpl label = getTitle(new RdfResource(rdfResource, node.toString()));
                manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(id), label));
            }
        }

        if (manifests.isEmpty()) {
            ResIterator resources = rdfResource.listResourcesWithPropertyWithId(PCDM_HAS_FILE_PREDICATE);
            while (resources.hasNext()) {
                Resource resource = resources.next();
                PropertyValueSimpleImpl label = getTitle(new RdfResource(resource.getModel(), resource));
                manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(resource.getURI()), label));
            }

        }

        return manifests;
    }

    private List<ManifestReference> gatherResourceManifests(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException, IOException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();

        Optional<String> firstId = getIdByPredicate(rdfResource, IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = getIdByPredicate(rdfResource, IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = rdfResource.getModel().getResource(firstId.get());
                gatherResourceManifests(request, new RdfOrderedResource(rdfResource.getModel(), firstResource, firstId.get(), lastId.get()), manifests);
            }
        }

        return manifests;
    }

    private void gatherResourceManifests(ManifestRequest request, RdfOrderedResource rdfOrderedResource, List<ManifestReference> manifests) throws IOException, URISyntaxException {

        Model model = getRdfModel(rdfOrderedResource.getResource().getURI());

        Optional<String> id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {
            PropertyValueSimpleImpl label = getTitle(rdfOrderedResource);
            manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(id.get()), label));

            Optional<String> nextId = getIdByPredicate(model, IANA_NEXT_PREDICATE);

            if (nextId.isPresent()) {
                Resource resource = rdfOrderedResource.getModel().getResource(nextId.get());
                rdfOrderedResource.setResource(resource);
                rdfOrderedResource.setCurrentId(nextId.get());
                gatherResourceManifests(request, rdfOrderedResource, manifests);
            }
        }

    }

    private Model getRdfModel(String url) throws NotFoundException {
        String rdf = httpService.get(url + FEDORA_FCR_METADATA);
        Optional<String> fedoraRdf = Optional.ofNullable(rdf);
        if (fedoraRdf.isPresent()) {
            return createRdfModel(fedoraRdf.get());
        }
        throw new NotFoundException("Fedora RDF not found!");
    }

    @Override
    protected ManifestType getManifestType() {
        return COLLECTION;
    }

}
