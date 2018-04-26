package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.CONTEXT_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_BITSTREAM_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_COMMUNITY_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_PART_OF_REPOSITORY_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_IS_SUB_COMMUNITY_OF_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_DESCRIPTION;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_TITLE;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static edu.tamu.iiif.model.RepositoryType.DSPACE;
import static edu.tamu.iiif.utility.RdfModelUtility.createRdfModel;
import static edu.tamu.iiif.utility.RdfModelUtility.getIdByPredicate;
import static edu.tamu.iiif.utility.RdfModelUtility.getObject;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

@Profile(DSPACE_IDENTIFIER)
public abstract class AbstractDSpaceManifestService extends AbstractManifestService {

    @Value("${iiif.dspace.url}")
    protected String dspaceUrl;

    @Value("${iiif.dspace.webapp}")
    protected String dspaceWebapp;

    protected RdfResource getDSpaceRdfModel(String handle) throws NotFoundException {
        String dspaceRdfUrl = getRdfUrl(handle);
        String rdf = getRdf(dspaceRdfUrl);
        Model model = createRdfModel(rdf);
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(dspaceRdfUrl));
    }

    protected Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);
        Sequence sequence = new SequenceImpl(getDSpaceIiifSequenceUri(handle), label);
        sequence.setCanvases(getCanvases(rdfResource));
        return sequence;
    }

    protected Canvas generateCanvas(RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);

        RdfCanvas rdfCanvas = getDSpaceRdfCanvas(rdfResource);

        Canvas canvas = new CanvasImpl(getDSpaceIiifCanvasUri(handle), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        canvas.setMetadata(new ArrayList<Metadata>());

        return canvas;
    }

    protected PropertyValueSimpleImpl getTitle(RdfResource rdfResource) {
        Optional<String> title = getObject(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        if (!title.isPresent()) {
            String id = rdfResource.getResource().getURI();
            title = Optional.of(getRepositoryPath(id));
        }
        return new PropertyValueSimpleImpl(title.get());
    }

    protected PropertyValueSimpleImpl getDescription(RdfResource rdfResource) {
        Optional<String> description = getObject(rdfResource, DUBLIN_CORE_TERMS_DESCRIPTION);
        if (!description.isPresent()) {
            description = Optional.of("N/A");
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
        return getDSpaceIiifUri(handle, COLLECECTION_IDENTIFIER);
    }

    protected URI getDSpaceIiifPresentationUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, PRESENTATION_IDENTIFIER);
    }

    protected URI getDSpaceIiifSequenceUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, SEQUENCE_IDENTIFIER);
    }

    protected URI getDSpaceIiifCanvasUri(String handle) throws URISyntaxException {
        return getDSpaceIiifUri(handle, CANVAS_IDENTIFIER);
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

    private URI getDSpaceIiifUri(String handle, String type) throws URISyntaxException {
        return URI.create(getIiifServiceUrl() + "/" + type + "?" + CONTEXT_IDENTIFIER + "=" + handle);
    }

    private String getRdf(String dspaceRdfUrl) throws NotFoundException {
        Optional<String> dspaceRdf = Optional.ofNullable(httpService.get(dspaceRdfUrl));
        if (dspaceRdf.isPresent()) {
            return dspaceRdf.get();
        }
        throw new NotFoundException("DSpace RDF not found!");
    }

    private List<Canvas> getCanvases(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();
        NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(DSPACE_HAS_BITSTREAM_PREDICATE);
        while (collectionIterator.hasNext()) {
            String uri = collectionIterator.next().toString();
            canvases.add(generateCanvas(new RdfResource(rdfResource, uri)));
        }
        return canvases;
    }

    private RdfCanvas getDSpaceRdfCanvas(RdfResource rdfResource) throws URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        RdfCanvas rdfCanvas = new RdfCanvas();
        String canvasId = getHandle(uri);

        RdfResource fileFedoraRdfResource = new RdfResource(rdfResource, uri);

        Image image = generateImage(fileFedoraRdfResource, canvasId);

        rdfCanvas.addImage(image);

        Optional<ImageResource> imageResource = Optional.ofNullable(image.getResource());

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

        return rdfCanvas;
    }

    protected URI getCanvasUri(String canvasId) throws URISyntaxException {
        return getDSpaceIiifCanvasUri(canvasId);
    }

    protected String getIiifImageServiceName() {
        return "DSpace IIIF Image Resource Service";
    }

}
