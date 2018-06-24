package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.constants.Constants.PCDM_HAS_MEMBER_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.COLLECTION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraPcdmCollectionManifestService extends AbstractFedoraPcdmManifestService {

    @Override
    protected String generateManifest(ManifestRequest request) throws URISyntaxException, IOException {
        String context = request.getContext();
        return mapper.writeValueAsString(generateCollection(context));
    }

    private Collection generateCollection(String context) throws URISyntaxException, IOException {
        RdfResource rdfResource = getRdfResource(context);

        URI id = buildId(context);

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

    private List<ManifestReference> getResourceManifests(RdfResource rdfResource) throws URISyntaxException, NotFoundException {
        List<ManifestReference> manifests = new ArrayList<ManifestReference>();
        List<String> members = getMembers(rdfResource);
        for (String id : members) {
            PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(getRepositoryPath(id));
            manifests.add(new ManifestReferenceImpl(getFedoraIiifPresentationUri(id), label));
        }
        return manifests;
    }

    private List<String> getMembers(RdfResource rdfResource) throws NotFoundException {

        List<String> members = new ArrayList<String>();

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

    @Override
    protected ManifestType getManifestType() {
        return COLLECTION;
    }

}
