package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_BITSTREAM_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_COMMUNITY_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_REPOSITORY_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_SUB_COMMUNITY_OF_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_RDF_CONDITION;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_ABSTRACT;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_DESCRIPTION;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_TITLE;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static edu.tamu.iiif.utility.RdfModelUtility.getIdByPredicate;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

@ConditionalOnExpression(DSPACE_RDF_CONDITION)
public abstract class AbstractDSpaceRdfManifestService extends AbstractManifestService {

    @Value("${iiif.dspace.url}")
    protected String dspaceUrl;

    @Value("${iiif.dspace.webapp}")
    protected String dspaceWebapp;

    @Value("${iiif.dspace.identifier.dspace-rdf}")
    protected String dspaceRdfIdentifier;

    protected Sequence generateSequence(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);
        Sequence sequence = new SequenceImpl(getDSpaceIiifSequenceUri(handle), label);
        sequence.setCanvases(getCanvases(request, rdfResource));
        return sequence;
    }

    protected Canvas generateCanvas(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(getBitstreamPath(uri));

        RdfCanvas rdfCanvas = getDSpaceRdfCanvas(request, rdfResource);

        Canvas canvas = new CanvasImpl(getDSpaceIiifCanvasUri(getHandlePath(uri)), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        return canvas;
    }

    protected PropertyValueSimpleImpl getTitle(RdfResource rdfResource) {
        Optional<String> title = getObject(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        if (!title.isPresent()) {
            String id = rdfResource.getResource().getURI();
            title = Optional.of(getRepositoryContextIdentifier(id));
        }
        return new PropertyValueSimpleImpl(title.get());
    }

    protected PropertyValueSimpleImpl getDescription(RdfResource rdfResource) {
        Optional<String> description = getObject(rdfResource, DUBLIN_CORE_TERMS_ABSTRACT);
        if (!description.isPresent()) {
            description = getObject(rdfResource, DUBLIN_CORE_TERMS_DESCRIPTION);
        }
        if (!description.isPresent()) {
            description = Optional.of("No description available!");
        }
        return new PropertyValueSimpleImpl(description.get());
    }

    protected boolean isTopLevelCommunity(Model model) {
        return getIdByPredicate(model, DSPACE_IS_PART_OF_REPOSITORY_PREDICATE).isPresent();
    }

    protected boolean isSubcommunity(Model model) {
        return getIdByPredicate(model, DSPACE_IS_SUB_COMMUNITY_OF_PREDICATE).isPresent();
    }

    protected boolean isCollection(Model model) {
        return getIdByPredicate(model, DSPACE_IS_PART_OF_COMMUNITY_PREDICATE).isPresent();
    }

    protected boolean isItem(Model model) {
        return getIdByPredicate(model, DSPACE_IS_PART_OF_COLLECTION_PREDICATE).isPresent();
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
        return iiifServiceUrl + "/" + dspaceRdfIdentifier;
    }

    @Override
    protected String getRepositoryContextIdentifier(String url) {
        return dspaceRdfIdentifier + ":" + getRepositoryPath(url);
    }

    @Override
    protected String getRepositoryPath(String url) {
        return url.substring(dspaceUrl.length() + 1);
    }

    @Override
    protected String getRepositoryType() {
        return dspaceRdfIdentifier;
    }

    @Override
    protected String getRdfUrl(String handle) {
        return joinPath(dspaceUrl, "rdf", "handle", handle);
    }

    @Override
    protected String getRdf(String dspaceRdfUrl) throws NotFoundException {
        Optional<String> dspaceRdf = Optional.ofNullable(httpService.get(dspaceRdfUrl));
        if (dspaceRdf.isPresent()) {
            return dspaceRdf.get();
        }
        throw new NotFoundException("DSpace RDF not found! " + dspaceRdfUrl);
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

    private RdfCanvas getDSpaceRdfCanvas(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        RdfCanvas rdfCanvas = new RdfCanvas();
        String canvasId = getHandlePath(uri);

        RdfResource fileFedoraRdfResource = new RdfResource(rdfResource, uri);

        Optional<Image> image = generateImage(request, fileFedoraRdfResource, canvasId);
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
