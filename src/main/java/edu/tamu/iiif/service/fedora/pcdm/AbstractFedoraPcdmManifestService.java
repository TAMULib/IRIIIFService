package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.FEDORA_FCR_METADATA;
import static edu.tamu.iiif.constants.Constants.FEDORA_PCDM_CONDITION;
import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.LDP_CONTAINS_PREDICATE;
import static edu.tamu.iiif.constants.Constants.LDP_HAS_MEMBER_RELATION_PREDICATE;
import static edu.tamu.iiif.constants.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_COLLECTION;
import static edu.tamu.iiif.constants.Constants.PCDM_FILE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_FILE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_MEMBER_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.RDF_TYPE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static edu.tamu.iiif.utility.RdfModelUtility.getObject;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.config.AbstractIiifConfig;
import edu.tamu.iiif.config.FedoraPcdmIiifConfig;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfOrderedResource;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;
import edu.tamu.iiif.utility.RdfModelUtility;

@ConditionalOnExpression(FEDORA_PCDM_CONDITION)
public abstract class AbstractFedoraPcdmManifestService extends AbstractManifestService {

    @Autowired
    protected FedoraPcdmIiifConfig config;

    protected Sequence generateSequence(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        String parameterizedId = RdfModelUtility.getParameterizedId(rdfResource.getResource().getURI(), request);
        PropertyValueSimpleImpl label = getLabel(rdfResource);
        Sequence sequence = new SequenceImpl(getFedoraIiifSequenceUri(parameterizedId), label);
        sequence.setCanvases(getCanvases(request, rdfResource));
        return sequence;
    }

    protected Canvas generateCanvas(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        String parameterizedId = RdfModelUtility.getParameterizedId(rdfResource.getResource().getURI(), request);
        PropertyValueSimpleImpl label = getLabel(rdfResource);

        RdfCanvas rdfCanvas = getFedoraRdfCanvas(request, rdfResource);

        Canvas canvas = new CanvasImpl(getFedoraIiifCanvasUri(parameterizedId), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        List<Metadata> metadata = getMetadata(rdfResource);

        if (!metadata.isEmpty()) {
            canvas.setMetadata(metadata);
        }

        return canvas;
    }

    protected URI getFedoraIiifCollectionUri(String url) throws URISyntaxException {
        return getFedoraIiifUri(url, COLLECTION_IDENTIFIER);
    }

    protected URI getFedoraIiifPresentationUri(String url) throws URISyntaxException {
        return getFedoraIiifUri(url, PRESENTATION_IDENTIFIER);
    }

    protected URI getFedoraIiifSequenceUri(String url) throws URISyntaxException {
        return getFedoraIiifUri(url, SEQUENCE_IDENTIFIER);
    }

    protected URI getFedoraIiifCanvasUri(String url) throws URISyntaxException {
        return getFedoraIiifUri(url, CANVAS_IDENTIFIER);
    }

    protected URI getCanvasUri(String canvasId) throws URISyntaxException {
        return getFedoraIiifCanvasUri(canvasId);
    }

    protected Model getFedoraRdfModel(String url) throws NotFoundException {
        return getRdfModel(url + FEDORA_FCR_METADATA);
    }

    protected boolean isCollection(RdfResource rdfResource) {
        NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(RDF_TYPE_PREDICATE);
        while (nodes.hasNext()) {
            RDFNode node = nodes.next();
            if (node.toString().equals(PCDM_COLLECTION)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isCollection(Model model) {
        NodeIterator nodes = model.listObjectsOfProperty(model.getProperty(RDF_TYPE_PREDICATE));
        while (nodes.hasNext()) {
            RDFNode node = nodes.next();
            if (node.toString().equals(PCDM_COLLECTION)) {
                return true;
            }
        }
        return false;
    }

    protected String getCollectionObjectsMember(RdfResource rdfResource) throws NotFoundException {
        NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);
        if (nodes.hasNext()) {
            RDFNode node = nodes.next();
            return node.toString();
        }
        throw new NotFoundException("Collection does not contain its expected member!");
    }

    protected String getIiifImageServiceName() {
        return "Fedora IIIF Image Resource Service";
    }

    @Override
    protected String getMatcherHandle(String uri) {
        return uri;
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
    protected String getRdfUrl(String path) {
        return joinPath(config.getUrl(), path);
    }

    @Override
    protected AbstractIiifConfig getConfig() {
        return config;
    }

    // TODO: update to match getDSpaceIiifUrl
    private URI getFedoraIiifUri(String url, String type) throws URISyntaxException {
        return URI.create(url.replace(config.getUrl() + "/", getIiifServiceUrl() + "/" + type + "/"));
    }

    private List<Canvas> getCanvases(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();

        Optional<String> firstId = getObject(rdfResource.getModel(), IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = getObject(rdfResource.getModel(), IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = rdfResource.getModel().getResource(firstId.get());
                generateOrderedCanvases(request, new RdfOrderedResource(rdfResource.getModel(), firstResource, firstId.get(), lastId.get()), canvases);
            }
        }

        if (canvases.isEmpty()) {
            NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);
            while (nodes.hasNext()) {
                RDFNode node = nodes.next();

                Model fileModel = getFedoraRdfModel(node.toString());

                RdfResource fileRdfResource = new RdfResource(fileModel, node.toString());

                if (fileRdfResource.getResourceById(PCDM_HAS_FILE_PREDICATE) != null) {
                    Canvas canvas = generateCanvas(request, fileRdfResource);
                    if (canvas.getImages().size() > 0) {
                        canvases.add(canvas);
                    }
                }
            }
        }

        if (canvases.isEmpty() && rdfResource.getResourceById(PCDM_HAS_FILE_PREDICATE) != null) {
            Canvas canvas = generateCanvas(request, rdfResource);
            if (canvas.getImages().size() > 0) {
                canvases.add(canvas);
            }
        }

        return canvases;
    }

    private void generateOrderedCanvases(ManifestRequest request, RdfOrderedResource rdfOrderedSequence, List<Canvas> canvases) throws IOException, URISyntaxException {

        Model model = getFedoraRdfModel(rdfOrderedSequence.getResource().getURI());

        Optional<String> id = getObject(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = getObject(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {

            Model orderedModel = getFedoraRdfModel(id.get());

            Canvas canvas = generateCanvas(request, new RdfResource(orderedModel, id.get()));
            if (canvas.getImages().size() > 0) {
                canvases.add(canvas);
            }

            Optional<String> nextId = getObject(model, IANA_NEXT_PREDICATE);

            if (nextId.isPresent()) {
                Resource resource = rdfOrderedSequence.getModel().getResource(nextId.get());
                rdfOrderedSequence.setResource(resource);
                rdfOrderedSequence.setCurrentId(nextId.get());
                generateOrderedCanvases(request, rdfOrderedSequence, canvases);
            }
        }

    }

    private RdfCanvas getFedoraRdfCanvas(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        RdfCanvas rdfCanvas = new RdfCanvas();

        String parameterizedCanvasId = RdfModelUtility.getParameterizedId(rdfResource.getResource().getURI(), request);

        Statement canvasStatement = rdfResource.getStatementOfPropertyWithId(LDP_CONTAINS_PREDICATE);

        String parentId = canvasStatement.getObject().toString();

        Model parentModel = getFedoraRdfModel(parentId);

        RdfResource parentRdfResource = new RdfResource(parentModel, parentId);

        if (parentRdfResource.containsStatement(LDP_HAS_MEMBER_RELATION_PREDICATE, PCDM_HAS_FILE_PREDICATE)) {
            NodeIterator nodeItr = parentRdfResource.getNodesOfPropertyWithId(LDP_CONTAINS_PREDICATE);
            while (nodeItr.hasNext()) {
                RDFNode node = nodeItr.next();

                Model fileModel = getFedoraRdfModel(node.toString());

                RdfResource fileRdfResource = new RdfResource(fileModel, node.toString());

                if (fileRdfResource.containsStatement(RDF_TYPE_PREDICATE, PCDM_FILE)) {
                    Optional<Image> image = generateImage(request, fileRdfResource, parameterizedCanvasId);
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
                }

            }
        }
        return rdfCanvas;
    }

}
