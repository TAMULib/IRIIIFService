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
import java.util.Optional;

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

        RdfResource rdfResource = getRdfResourceByContextPath(context);

        URI id = buildId(parameterizedContext);

        List<Metadata> metadata = getMetadata(rdfResource);

        Collection collection = new CollectionImpl(id, getLabel(rdfResource), metadata);

        collection.setManifests(getResourceManifests(request, rdfResource));

        List<CollectionReference> collections = getSubcollections(rdfResource);
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

    private List<ManifestReference> getResourceManifests(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException, NotFoundException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        if (isItem(rdfResource.getModel())) {
            NodeIterator hasBitstreamIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_BITSTREAM_PREDICATE);
            while (hasBitstreamIterator.hasNext()) {
                String hasBitstreamHandlePath = getHandlePath(hasBitstreamIterator.next().toString());
                String parameterizedHasBitstreamHandlePath = RdfModelUtility.getParameterizedId(hasBitstreamHandlePath, request);
                RdfResource hasBitstreamRdfResource = getRdfResourceByContextPath(hasBitstreamHandlePath);
                manifests.add(new ManifestReferenceImpl(getDSpaceIiifPresentationUri(parameterizedHasBitstreamHandlePath), getLabel(hasBitstreamRdfResource)));
            }
        } else {
            NodeIterator hasItemIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_ITEM_PREDICATE);
            while (hasItemIterator.hasNext()) {
                String hasItemHandle = getHandle(hasItemIterator.next().toString());
                String parameterizedHasItemHandle = RdfModelUtility.getParameterizedId(hasItemHandle, request);
                RdfResource hasItemRdfResource = getRdfResourceByContextPath(hasItemHandle);
                manifests.add(new ManifestReferenceImpl(getDSpaceIiifPresentationUri(parameterizedHasItemHandle), getLabel(hasItemRdfResource)));
            }
        }
        return manifests;
    }

    private List<CollectionReference> getSubcollections(RdfResource rdfResource) throws URISyntaxException, NotFoundException {
        List<CollectionReference> collectionsToElide = new ArrayList<CollectionReference>();
        List<CollectionReference> subcollections = getSubcommunities(rdfResource, collectionsToElide);
        List<CollectionReference> collections = getCollections(rdfResource);
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

    private List<CollectionReference> getSubcommunities(RdfResource rdfResource, List<CollectionReference> collectionsToElide) throws URISyntaxException, NotFoundException {
        List<CollectionReference> subcommunities = new ArrayList<CollectionReference>();
        NodeIterator subcommunityIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_SUB_COMMUNITY_PREDICATE);
        while (subcommunityIterator.hasNext()) {
            String subcommunityHandle = getHandle(subcommunityIterator.next().toString());
            RdfResource subcommunityRdfResource = getRdfResourceByContextPath(subcommunityHandle);
            collectionsToElide.addAll(getCollections(subcommunityRdfResource));
            subcommunities.add(new CollectionReferenceImpl(getDSpaceIiifCollectionUri(subcommunityHandle), getLabel(subcommunityRdfResource)));
        }
        return subcommunities;
    }

    private List<CollectionReference> getCollections(RdfResource rdfResource) throws URISyntaxException, NotFoundException {
        List<CollectionReference> collections = new ArrayList<CollectionReference>();
        NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_COLLECTION_PREDICATE);
        while (collectionIterator.hasNext()) {
            String collectionHandle = getHandle(collectionIterator.next().toString());
            RdfResource collectionRdfResource = getRdfResourceByContextPath(collectionHandle);
            collections.add(new CollectionReferenceImpl(getDSpaceIiifCollectionUri(collectionHandle), getLabel(collectionRdfResource)));
        }
        return collections;
    }

    @Override
    public ManifestType getManifestType() {
        return COLLECTION;
    }

}
