package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static edu.tamu.iiif.model.RepositoryType.DSPACE;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.constants.Constants;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

@Profile(DSPACE_IDENTIFIER)
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

    protected Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);
        Sequence sequence = new SequenceImpl(getDSpaceIIIFSequenceUri(handle), label);
        sequence.setCanvases(getCanvases(rdfResource));
        return sequence;
    }

    private List<Canvas> getCanvases(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();
        NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        while (collectionIterator.hasNext()) {
            String uri = collectionIterator.next().toString();
            canvases.add(generateCanvas(new RdfResource(rdfResource, uri)));
        }
        return canvases;
    }

    protected Canvas generateCanvas(RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);

        RdfCanvas rdfCanvas = getDSpaceRdfCanvas(rdfResource);

        Canvas canvas = new CanvasImpl(getDSpaceIIIFCanvasUri(handle), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        canvas.setMetadata(new ArrayList<Metadata>());

        return canvas;
    }

    private RdfCanvas getDSpaceRdfCanvas(RdfResource rdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String uri = rdfResource.getResource().getURI();
        RdfCanvas rdfCanvas = new RdfCanvas();
        String canvasId = getHandle(uri);

        RdfResource fileFedoraRdfResource = new RdfResource(rdfResource, uri);

        Image image = generateImage(fileFedoraRdfResource, canvasId);

        rdfCanvas.addImage(image);

        int height = image.getResource().getHeight();
        if (height > rdfCanvas.getHeight()) {
            rdfCanvas.setHeight(height);
        }

        int width = image.getResource().getWidth();
        if (width > rdfCanvas.getWidth()) {
            rdfCanvas.setWidth(width);
        }
        return rdfCanvas;
    }

    private Image generateImage(RdfResource rdfResource, String canvasId) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String url = rdfResource.getResource().getURI();
        Image image = new ImageImpl(getImageInfoUri(url));
        image.setResource(generateImageResource(rdfResource));
        image.setOn(getDSpaceIIIFCanvasUri(canvasId));
        return image;
    }

    private ImageResource generateImageResource(RdfResource rdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String url = rdfResource.getResource().getURI();
        ImageResource imageResource = new ImageResourceImpl(getImageFullUrl(url));

        URI infoUri = getImageInfoUri(url);

        JsonNode imageInfoNode = getImageInfo(infoUri.toString());

        imageResource.setHeight(imageInfoNode.get("height").asInt());

        imageResource.setWidth(imageInfoNode.get("width").asInt());

        imageResource.setServices(getServices(rdfResource, "DSpace IIIF Image Resource Service"));

        return imageResource;
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
        return getDSpaceIiifUri(handle, COLLECECTION_IDENTIFIER);
    }

    protected URI getDSpaceIIIFPresentationUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, PRESENTATION_IDENTIFIER);
    }

    protected URI getDSpaceIIIFSequenceUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, SEQUENCE_IDENTIFIER);
    }

    protected URI getDSpaceIIIFCanvasUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, CANVAS_IDENTIFIER);
    }

    protected URI getDSpaceIIIFImageUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, IMAGE_IDENTIFIER);
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

    private URI getDSpaceIiifUri(String handle, String type) throws URISyntaxException {
        return URI.create(getIiifServiceUrl() + "/" + type + "?path=" + handle);
    }

    private Model generateRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

}
