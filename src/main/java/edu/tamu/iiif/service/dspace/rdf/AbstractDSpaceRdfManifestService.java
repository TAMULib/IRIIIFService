package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_BITSTREAM_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_COMMUNITY_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_REPOSITORY_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_SUB_COMMUNITY_OF_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_RDF_CONDITION;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static edu.tamu.iiif.utility.RdfModelUtility.getObject;
import static edu.tamu.iiif.utility.StringUtility.encodeSpaces;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.config.model.AbstractIiifConfig;
import edu.tamu.iiif.config.model.DSpaceRdfIiifConfig;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;
import edu.tamu.iiif.utility.RdfModelUtility;

@ConditionalOnExpression(DSPACE_RDF_CONDITION)
public abstract class AbstractDSpaceRdfManifestService extends AbstractManifestService {

    @Autowired
    protected DSpaceRdfIiifConfig config;

    protected Sequence generateSequence(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);
        String parameterizedHandle = RdfModelUtility.getParameterizedId(handle, request);
        Sequence sequence = new SequenceImpl(getDSpaceIiifSequenceUri(parameterizedHandle), label);
        sequence.setCanvases(getCanvases(request, rdfResource));
        return sequence;
    }

    protected Canvas generateCanvas(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(getBitstreamPath(uri));
        String parameterizedUri = RdfModelUtility.getParameterizedId(uri, request);
        RdfCanvas rdfCanvas = getDSpaceRdfCanvas(request, rdfResource);
        Canvas canvas = new CanvasImpl(getDSpaceIiifCanvasUri(getHandlePath(parameterizedUri)), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());
        canvas.setImages(rdfCanvas.getImages());
        return canvas;
    }

    protected boolean isTopLevelCommunity(Model model) {
        return getObject(model, DSPACE_IS_PART_OF_REPOSITORY_PREDICATE).isPresent();
    }

    protected boolean isSubcommunity(Model model) {
        return getObject(model, DSPACE_IS_SUB_COMMUNITY_OF_PREDICATE).isPresent();
    }

    protected boolean isCollection(Model model) {
        return getObject(model, DSPACE_IS_PART_OF_COMMUNITY_PREDICATE).isPresent();
    }

    protected boolean isItem(Model model) {
        return getObject(model, DSPACE_IS_PART_OF_COLLECTION_PREDICATE).isPresent();
    }

    protected URI getDSpaceIiifCollectionUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(encodeSpaces(handle), COLLECTION_IDENTIFIER);
    }

    protected URI getDSpaceIiifPresentationUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(encodeSpaces(handle), PRESENTATION_IDENTIFIER);
    }

    protected URI getDSpaceIiifSequenceUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(encodeSpaces(handle), SEQUENCE_IDENTIFIER);
    }

    protected URI getDSpaceIiifCanvasUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(encodeSpaces(handle), CANVAS_IDENTIFIER);
    }

    protected String getHandle(String uri) {
        String[] parts = getHandlePath(uri).split("/");
        return parts[0] + "/" + parts[1];
    }

    protected String getBitstreamPath(String uri) throws UnsupportedEncodingException {
        String[] parts = getHandlePath(uri).split("/");
        String bitstreamPath = "";
        for (int i = 2; i < parts.length; i++) {
            bitstreamPath += parts[i];
        }
        return URLDecoder.decode(bitstreamPath, "UTF-8");
    }

    protected String getHandlePath(String uri) {
        String path;
        if (uri.contains("/handle/")) {
            path = uri.split("/handle/")[1];
        } else if (uri.contains("/bitstream/")) {
            path = uri.split("/bitstream/")[1];
        } else {
            path = uri.split("/resource/")[1];
        }
        return path;
    }

    protected URI getCanvasUri(String canvasId) throws URISyntaxException {
        return getDSpaceIiifCanvasUri(canvasId);
    }

    protected String getIiifImageServiceName() {
        return "DSpace IIIF Image Resource Service";
    }

    @Override
    protected String getMatcherHandle(String uri) {
        return getHandle(uri);
    }

    @Override
    protected String getIiifServiceUrl() {
        return iiifServiceUrl + "/" + config.getIdentifier();
    }

    @Override
    protected String getRepositoryContextIdentifier(String url) {
        return config.getIdentifier() + ":" + getRepositoryPath(url);
    }

    @Override
    protected String getRepositoryPath(String url) {
        return url.substring(config.getUrl().length() + 1);
    }

    @Override
    public String getRepository() {
        return config.getIdentifier();
    }

    @Override
    protected String getRdfUrl(String handle) {
        return joinPath(config.getUrl(), "rdf", "handle", handle);
    }

    @Override
    protected AbstractIiifConfig getConfig() {
        return config;
    }

    private URI getDSpaceIiifUri(String handle, String type) throws URISyntaxException {
        return URI.create(getIiifServiceUrl() + "/" + type + "/" + handle);
    }

    private List<Canvas> getCanvases(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();
        // NOTE: canvas per bitstream and bitstreams uri must contain the context handle path of the desired resource
        String contextHandlePath = encodeSpaces(getHandlePath(rdfResource.getId()));
        NodeIterator bitstreamIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_BITSTREAM_PREDICATE);
        while (bitstreamIterator.hasNext()) {
            String uri = bitstreamIterator.next().toString();
            if (uri.contains(contextHandlePath)) {
                Canvas canvas = generateCanvas(request, new RdfResource(rdfResource, uri));
                if (canvas.getImages().size() > 0) {
                    canvases.add(canvas);
                }
            }
        }
        return canvases;
    }

    private RdfCanvas getDSpaceRdfCanvas(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException, InvalidUrlException {
        String uri = rdfResource.getResource().getURI();
        RdfCanvas rdfCanvas = new RdfCanvas();
        String parameterizedCanvasId = RdfModelUtility.getParameterizedId(getHandlePath(uri), request);

        RdfResource fileFedoraRdfResource = new RdfResource(rdfResource, uri);

        Optional<Image> image = generateImage(request, fileFedoraRdfResource, parameterizedCanvasId);
        if (image.isPresent()) {

            rdfCanvas.addImage(image.get());

            Optional<ImageResource> imageResource = Optional.ofNullable(image.get().getResource());

            if (imageResource.isPresent()) {
                int height = imageResource.get().getHeight();
                if (height > rdfCanvas.getHeight()) {
                    rdfCanvas.setHeight(height);
                }

                int width = imageResource.get().getWidth();
                if (width > rdfCanvas.getWidth()) {
                    rdfCanvas.setWidth(width);
                }
            }
        }

        return rdfCanvas;
    }

}
