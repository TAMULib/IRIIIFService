package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_BITSTREAM_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_COLLECTION_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_ITEM_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_SUB_COMMUNITY_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.COLLECTION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.NodeIterator;
import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Collection;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.references.CollectionReference;
import de.digitalcollections.iiif.presentation.model.api.v2.references.ManifestReference;
import de.digitalcollections.iiif.presentation.model.impl.v2.CollectionImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.CollectionReferenceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.references.ManifestReferenceImpl;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.utility.RdfModelUtility;

@Service
public class DSpaceRdfCollectionManifestService extends AbstractDSpaceRdfManifestService {

    @Override
    protected String generateManifest(ManifestRequest request) throws URISyntaxException, IOException {
        return mapper.writeValueAsString(generateCollection(request));
    }

    private Collection generateCollection(ManifestRequest request) throws URISyntaxException, NotFoundException {
        String context = request.getContext();

        String parameterizedContext = RdfModelUtility.getParameterizedId(request);

        RdfResource rdfResource = getRdfResource(context);

        URI id = buildId(parameterizedContext);

        PropertyValueSimpleImpl label = getLabel(rdfResource);

        List<Metadata> metadata = getMetadata(rdfResource);

        Collection collection = new CollectionImpl(id, label, metadata);

        List<CollectionReference> collections = getSubcollections(request, rdfResource);
        if (!collections.isEmpty()) {
            collection.setSubCollections(collections);
        }

        collection.setManifests(getResourceManifests(request, rdfResource));

        collection.setDescription(getDescription(rdfResource));

        collection.setLogo(getLogo(rdfResource));

        collection.setViewingHint("multi-part");

        return collection;
    }

    private List<CollectionReference> getSubcollections(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException, NotFoundException {
        List<CollectionReference> subcollections = getSubcommunities(rdfResource);

        List<CollectionReference> collections = getCollections(rdfResource);

        List<CollectionReference> collectionsToElide = getCollectionsToElide(rdfResource);

        collections.forEach(c -> {
            boolean include = true;
            for (CollectionReference ce : collectionsToElide) {
                if (c.getId().toString().equals(ce.getId().toString())) {
                    include = false;
                    break;
                }
            }
            if (include) {
                subcollections.add(c);
            }
        });

        return subcollections;
    }

    private List<CollectionReference> getSubcommunities(RdfResource rdfResource) throws URISyntaxException {
        List<CollectionReference> subcommunities = new ArrayList<CollectionReference>();
        NodeIterator subcommunityIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_SUB_COMMUNITY_PREDICATE);
        while (subcommunityIterator.hasNext()) {
            String uri = subcommunityIterator.next().toString();
            String handle = getHandle(uri);
            subcommunities.add(new CollectionReferenceImpl(getDSpaceIiifCollectionUri(handle), new PropertyValueSimpleImpl(handle)));
        }
        return subcommunities;
    }

    private List<CollectionReference> getCollections(RdfResource rdfResource) throws URISyntaxException {
        List<CollectionReference> collections = new ArrayList<CollectionReference>();
        NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_COLLECTION_PREDICATE);
        while (collectionIterator.hasNext()) {
            String uri = collectionIterator.next().toString();
            String handle = getHandle(uri);
            collections.add(new CollectionReferenceImpl(getDSpaceIiifCollectionUri(handle), new PropertyValueSimpleImpl(handle)));
        }
        return collections;
    }

    private List<CollectionReference> getCollectionsToElide(RdfResource rdfResource) throws URISyntaxException, NotFoundException {
        List<CollectionReference> collectionsToElide = new ArrayList<CollectionReference>();
        for (CollectionReference subcommunity : getSubcommunities(rdfResource)) {
            String handle = subcommunity.getLabel().getFirstValue();
            RdfResource subcommunityRdfResource = getRdfResource(handle);
            collectionsToElide.addAll(getCollections(subcommunityRdfResource));
        }
        return collectionsToElide;
    }

    private List<ManifestReference> getResourceManifests(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        if (isItem(rdfResource.getModel())) {
            NodeIterator bitstreamIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_BITSTREAM_PREDICATE);
            while (bitstreamIterator.hasNext()) {
                String bitstreamHandlePath = getHandlePath(bitstreamIterator.next().toString());
                String parameterizedBitstreamHandlePath = RdfModelUtility.getParameterizedId(bitstreamHandlePath, request);
                manifests.add(new ManifestReferenceImpl(getDSpaceIiifPresentationUri(parameterizedBitstreamHandlePath), getLabel(rdfResource)));
            }
        } else {
            NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_ITEM_PREDICATE);
            while (collectionIterator.hasNext()) {
                String uri = collectionIterator.next().toString();
                String handle = getHandle(uri);
                String parameterizedHandle = RdfModelUtility.getParameterizedId(handle, request);
                manifests.add(new ManifestReferenceImpl(getDSpaceIiifPresentationUri(parameterizedHandle), getLabel(rdfResource)));
            }
        }
        return manifests;
    }

    @Override
    protected ManifestType getManifestType() {
        return COLLECTION;
    }

}
