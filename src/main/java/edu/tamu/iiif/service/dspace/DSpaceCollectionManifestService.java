package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.COLLECTION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
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
import edu.tamu.iiif.constants.rdf.Constants;
import edu.tamu.iiif.model.ManifestType;

@Service
public class DSpaceCollectionManifestService extends AbstractDSpaceManifestService {

    @Override
    protected String generateManifest(String handle) throws URISyntaxException, IOException {
        return mapper.writeValueAsString(generateCollection(handle));
    }

    private Collection generateCollection(String handle) throws URISyntaxException {
        Model model = getDSpaceRdfModel(handle);

        URI id = buildId(handle);

        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);

        List<Metadata> metadata = new ArrayList<Metadata>();

        Collection collection = new CollectionImpl(id, label, metadata);

        collection.setSubCollections(getSubcollections(model));

        collection.setManifests(getResourceManifests(model));

        collection.setDescription(getDescription(model));

        collection.setLogo(getLogo(model));

        collection.setViewingHint("multi-part");

        return collection;
    }

    private List<CollectionReference> getSubcollections(Model model) throws URISyntaxException {
        List<CollectionReference> subcollections = getSubcommunities(model);

        List<CollectionReference> collections = getCollections(model);

        List<CollectionReference> collectionsToElide = getCollectionsToElide(model);

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

    private List<CollectionReference> getSubcommunities(Model model) throws URISyntaxException {
        List<CollectionReference> subcommunities = new ArrayList<CollectionReference>();
        NodeIterator subcommunityIterator = model.listObjectsOfProperty(model.getProperty(Constants.DSPACE_HAS_SUB_COMMUNITY_PREDICATE));
        while (subcommunityIterator.hasNext()) {
            String uri = subcommunityIterator.next().toString();
            String handle = getHandle(uri);
            subcommunities.add(new CollectionReferenceImpl(URI.create(getIiifServiceUrl() + "/collection?path=" + handle), new PropertyValueSimpleImpl(handle)));
        }
        return subcommunities;
    }

    private List<CollectionReference> getCollections(Model model) throws URISyntaxException {
        List<CollectionReference> collections = new ArrayList<CollectionReference>();
        NodeIterator collectionIterator = model.listObjectsOfProperty(model.getProperty(Constants.DSPACE_HAS_COLLECTION_PREDICATE));
        while (collectionIterator.hasNext()) {
            String uri = collectionIterator.next().toString();
            String handle = getHandle(uri);
            collections.add(new CollectionReferenceImpl(URI.create(getIiifServiceUrl() + "/collection?path=" + handle), new PropertyValueSimpleImpl(handle)));
        }
        return collections;
    }

    private List<CollectionReference> getCollectionsToElide(Model model) throws URISyntaxException {
        List<CollectionReference> collectionsToElide = new ArrayList<CollectionReference>();
        for (CollectionReference subcommunity : getSubcommunities(model)) {
            String handle = subcommunity.getLabel().getFirstValue();
            Model subcommunityModel = getDSpaceRdfModel(handle);
            collectionsToElide.addAll(getCollections(subcommunityModel));
        }
        return collectionsToElide;
    }

    private List<ManifestReference> getResourceManifests(Model model) throws URISyntaxException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        NodeIterator collectionIterator = model.listObjectsOfProperty(model.getProperty(Constants.DSPACE_HAS_ITEM_PREDICATE));
        while (collectionIterator.hasNext()) {
            String uri = collectionIterator.next().toString();
            String handle = getHandle(uri);
            manifests.add(new ManifestReferenceImpl(URI.create(getIiifServiceUrl() + "/presentation?path=" + handle), new PropertyValueSimpleImpl(handle)));
        }
        return manifests;
    }

    @Override
    protected ManifestType getManifestType() {
        return COLLECTION;
    }

}
