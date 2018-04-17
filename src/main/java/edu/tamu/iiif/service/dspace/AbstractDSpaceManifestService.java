package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.constants.rdf.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.IMAGE_IDENTIFIER;
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
        String dspaceRdfUri = getRdfUrl(handle);
        String rdf = getRdf(dspaceRdfUri);
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

    protected boolean hasItems(Model model) {
        return getIdByPredicate(model, Constants.DSPACE_HAS_ITEM_PREDICATE).isPresent();
    }

    protected boolean isItem(Model model) {
        return getIdByPredicate(model, Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE).isPresent();
    }

    protected boolean isCommunity(Model model) {
        return isTopLevelCommunity(model) || isSubcommunity(model);
    }

    protected PropertyValueSimpleImpl getDescription(RdfResource rdfResource) {
        return new PropertyValueSimpleImpl("");
    }

    protected URI getDSpaceIIIFCollectionUri(String handle) throws URISyntaxException {
        return getDSpaceIIIFUri(handle, COLLECECTION_IDENTIFIER);
    }

    protected URI getDSpaceIIIFPresentationUri(String handle) throws URISyntaxException {
        return getDSpaceIIIFUri(handle, PRESENTATION_IDENTIFIER);
    }

    protected URI getDSpaceIIIFImageUri(String handle, String filename) throws URISyntaxException {
        return getDSpaceIIIFUri(handle, IMAGE_IDENTIFIER);
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

    private String getRdfUrl(String handle) {
        return joinPath(dspaceUrl, "rdf", "handle", handle);
    }

    private String getRdf(String dspaceRdfUri) {
        return httpService.get(dspaceRdfUri);
    }

    private URI getDSpaceIIIFUri(String handle, String type) throws URISyntaxException {
        return URI.create(getIiifServiceUrl() + "/" + type + "?path=" + handle);
    }

    private Model generateRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

}
