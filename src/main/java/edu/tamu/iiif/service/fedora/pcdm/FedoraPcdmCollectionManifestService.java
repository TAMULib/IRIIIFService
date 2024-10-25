package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_FILE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_MEMBER_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.COLLECTION;
import static edu.tamu.iiif.utility.RdfModelUtility.findObject;

import de.digitalcollections.iiif.presentation.model.api.v2.Collection;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.references.CollectionReference;
import de.digitalcollections.iiif.presentation.model.api.v2.references.ManifestReference;
import de.digitalcollections.iiif.presentation.model.impl.v2.CollectionImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.CollectionReferenceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.ManifestReferenceImpl;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfOrderedResource;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.utility.RdfModelUtility;
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

@Service
public class FedoraPcdmCollectionManifestService extends AbstractFedoraPcdmManifestService {

    @Override
    protected String generateManifest(ManifestRequest request) throws URISyntaxException, IOException {
        return mapper.writeValueAsString(generateCollection(request));
    }

    private Collection generateCollection(ManifestRequest request) throws URISyntaxException, IOException {
        String context = request.getContext();

        String parameterizedContext = RdfModelUtility.getParameterizedId(request);

        RdfResource rdfResource = getRdfResourceByContextPath(context);

        URI id = buildId(parameterizedContext);

        List<Metadata> metadata = getMetadata(rdfResource);

        Collection collection = new CollectionImpl(id, getLabel(rdfResource), metadata);

        boolean isCollection = isCollection(rdfResource);

        // NOTE: this will check for members if if not a collection container
        // it may be desired to require subcollections to be in a collection container
        // also, this is done before switching to the objects container as to have redundant rdf model lookups
        List<CollectionReference> collections = getSubcollections(rdfResource);

        if (isCollection) {
            // if container is a collection have to use objects container for rdf resource
            // as it contains metadata and iana proxies
            String collectionObjectMemberId = getCollectionObjectsMember(rdfResource);
            Model collectionObjectMemberModel = getFedoraRdfModel(collectionObjectMemberId);
            rdfResource = new RdfResource(collectionObjectMemberModel, collectionObjectMemberId);
        }

        collection.setManifests(getResourceManifests(request, rdfResource, isCollection));

        if (!collections.isEmpty()) {
            collection.setSubCollections(collections);
        }

        Optional<PropertyValueSimpleImpl> description = getDescription(rdfResource);
        if (description.isPresent()) {
            collection.setDescription(description.get());
        }

        Optional<PropertyValueSimpleImpl> attribution = getAttribution(rdfResource);
        if (attribution.isPresent()) {
            collection.setAttribution(attribution.get());
        }

        collection.setLogo(getLogo(rdfResource));

        collection.setViewingHint("multi-part");

        return collection;
    }

    private List<ManifestReference> getResourceManifests(ManifestRequest request, RdfResource rdfResource, boolean isCollection) throws URISyntaxException, IOException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        if (isCollection) {
            manifests.addAll(gatherResourceManifests(request, rdfResource));
        } else {
            NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);
            while (nodes.hasNext()) {
                RDFNode node = nodes.next();
                String parameterizedId = RdfModelUtility.getParameterizedId(node.toString(), request);
                RdfResource hasMemberRdfResource = getRdfResourceByUrl(node.toString());
                manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(parameterizedId), getLabel(hasMemberRdfResource)));
            }
        }
        if (manifests.isEmpty()) {
            ResIterator resources = rdfResource.listResourcesWithPropertyWithId(PCDM_HAS_FILE_PREDICATE);
            while (resources.hasNext()) {
                Resource resource = resources.next();
                String resourceUrl = resource.getURI();
                if (includeResourceWithUrl(request, resourceUrl)) {
                    RdfResource hasFileRdfResource = getRdfResourceByUrl(resourceUrl);
                    String parameterizedId = RdfModelUtility.getParameterizedId(resourceUrl, request);
                    manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(parameterizedId), getLabel(hasFileRdfResource)));
                }
            }
        }
        return manifests;
    }

    private List<ManifestReference> gatherResourceManifests(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException, IOException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();

        Optional<String> firstId = findObject(rdfResource.getModel(), IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = findObject(rdfResource.getModel(), IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = rdfResource.getModel().getResource(firstId.get());
                gatherResourceManifests(request, new RdfOrderedResource(rdfResource.getModel(), firstResource, firstId.get(), lastId.get()), manifests);
            }
        }

        return manifests;
    }

    private void gatherResourceManifests(ManifestRequest request, RdfOrderedResource rdfOrderedResource, List<ManifestReference> manifests) throws IOException, URISyntaxException {

        Model model = getFedoraRdfModel(rdfOrderedResource.getResource().getURI());

        Optional<String> id = findObject(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = findObject(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {

            String parameterizedActualId = RdfModelUtility.getParameterizedId(id.get(), request);

            manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(parameterizedActualId), getLabel(getRdfResourceByUrl(id.get()))));

            Optional<String> nextId = findObject(model, IANA_NEXT_PREDICATE);

            if (nextId.isPresent()) {
                Resource resource = rdfOrderedResource.getModel().getResource(nextId.get());
                rdfOrderedResource.setResource(resource);
                rdfOrderedResource.setCurrentId(nextId.get());
                gatherResourceManifests(request, rdfOrderedResource, manifests);
            }
        }

    }

    private List<CollectionReference> getSubcollections(RdfResource rdfResource) throws URISyntaxException, IOException {
        List<CollectionReference> subcollections = new ArrayList<CollectionReference>();
        // TODO: follow order proxy if iana available
        NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);
        while (nodes.hasNext()) {
            RDFNode node = nodes.next();
            String id = node.toString();
            Model member = getFedoraRdfModel(id);
            if (isCollection(member)) {
                PropertyValueSimpleImpl label = getLabel(new RdfResource(member, id));
                subcollections.add(new CollectionReferenceImpl(getFedoraIiifCollectionUri(id), label));
            }
        }
        return subcollections;
    }

    @Override
    public ManifestType getManifestType() {
        return COLLECTION;
    }

}
