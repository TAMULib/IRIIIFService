package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.constants.rdf.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.model.RepositoryType.DSPACE;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Value;

import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import edu.tamu.iiif.constants.rdf.Constants;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

public abstract class AbstractDSpaceManifestService extends AbstractManifestService {

    @Value("${iiif.dspace.url}")
    protected String dspaceUrl;

    protected RdfResource getDSpaceRdfModel(String handle) {
        String dspaceRdfUri = getId(handle);
        String rdf = getRdf(dspaceRdfUri);
        System.out.println("\n" + rdf + "\n");
        Model model = generateRdfModel(rdf);
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(dspaceRdfUri));
    }

    protected boolean isTopLevelCommunity(Model model) {
        return getIdByPredicate(model, Constants.DSPACE_IS_PART_OF_REPOSITORY_PREDICATE).isPresent();
    }

    protected boolean isSubcommunity(Model model) {
        return getIdByPredicate(model, Constants.DSPACE_IS_SUB_COMMUNITY_OF_PREDICATE).isPresent();
    }

    protected boolean hasCollections(Model model) {
        return getIdByPredicate(model, Constants.DSPACE_HAS_COLLECTION_PREDICATE).isPresent();
    }

    protected boolean isCollection(Model model) {
        return getIdByPredicate(model, Constants.DSPACE_IS_PART_OF_COMMUNITY_PREDICATE).isPresent();
    }

    protected boolean isCommunity(Model model) {
        return isTopLevelCommunity(model) || isSubcommunity(model);
    }

    protected PropertyValueSimpleImpl getDescription(RdfResource rdfResource) {
        return new PropertyValueSimpleImpl("");
    }

    protected String getHandle(String uri) {
        String handle;
        if (uri.contains("/handle/")) {
            handle = uri.split("/handle/")[1];
        } else {
            handle = uri.split("/resource/")[1];
        }
        return handle;
    }

    protected URI getDSpaceIIIFCollectionUri(String handle) throws URISyntaxException {
        return URI.create(getIiifServiceUrl() + "/" + COLLECECTION_IDENTIFIER + "?path=" + handle);
    }

    protected URI getDSpaceIIIFPresentationUri(String handle) throws URISyntaxException {
        return URI.create(getIiifServiceUrl() + "/" + PRESENTATION_IDENTIFIER + "?path=" + handle);
    }

    protected URI getImageUri(String id) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(id)));
    }

    @Override
    protected String getIiifServiceUrl() {
        return iiifServiceUrl + "/" + DSPACE_IDENTIFIER;
    }

    @Override
    protected String getRepositoryPath(String url) {
        return DSPACE_IDENTIFIER + ":" + url.substring(dspaceUrl.length() + 1);
    }

    @Override
    protected RepositoryType getRepositoryType() {
        return DSPACE;
    }

    private String getId(String handle) {
        return joinPath(dspaceUrl, "handle", handle);
    }

    private String getRdf(String dspaceRdfUri) {
        return httpService.get(dspaceRdfUri);
    }

    private Model generateRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

}
