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
import static edu.tamu.iiif.utility.RdfModelUtility.findObject;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.config.model.AbstractIiifConfig;
import edu.tamu.iiif.config.model.FedoraPcdmIiifConfig;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.CanvasWithInfo;
import edu.tamu.iiif.model.OptionalImageWithInfo;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfOrderedResource;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;
import edu.tamu.iiif.utility.RdfModelUtility;
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

    protected CanvasWithInfo generateCanvas(ManifestRequest request, RdfResource rdfResource, int page) throws IOException, URISyntaxException {
        String parameterizedId = RdfModelUtility.getParameterizedId(rdfResource.getResource().getURI(), request);
        PropertyValueSimpleImpl label = getLabel(rdfResource);
        RdfCanvas rdfCanvas = getFedoraRdfCanvas(request, rdfResource, page);
        Canvas canvas = new CanvasImpl(getFedoraIiifCanvasUri(parameterizedId), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());
        canvas.setImages(rdfCanvas.getImages());
        List<Metadata> metadata = getMetadata(rdfResource);
        if (!metadata.isEmpty()) {
            canvas.setMetadata(metadata);
        }
        if (rdfCanvas.getImagesInfo().isEmpty()) {
            return CanvasWithInfo.of(canvas, Optional.empty());
        }

        return CanvasWithInfo.of(canvas, rdfCanvas.getImagesInfo().get(0));
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
        Optional<String> firstId = findObject(rdfResource.getModel(), IANA_FIRST_PREDICATE);
        Optional<String> lastId = findObject(rdfResource.getModel(), IANA_LAST_PREDICATE);
        if (firstId.isPresent() && lastId.isPresent()) {
            Resource firstResource = rdfResource.getModel().getResource(firstId.get());
            generateOrderedCanvases(request, new RdfOrderedResource(rdfResource.getModel(), firstResource, firstId.get(), lastId.get()), canvases);
        }
        if (canvases.isEmpty()) {
            NodeIterator nodes = rdfResource.getNodesOfPropertyWithId(PCDM_HAS_MEMBER_PREDICATE);
            while (nodes.hasNext()) {
                RDFNode node = nodes.next();

                Model fileModel = getFedoraRdfModel(node.toString());

                RdfResource fileRdfResource = new RdfResource(fileModel, node.toString());

                if (fileRdfResource.getResourceById(PCDM_HAS_FILE_PREDICATE) != null) {
                    CanvasWithInfo canvas = generateCanvas(request, fileRdfResource, 0);
                    if (canvas.getCanvas().getImages().size() > 0) {
                        canvases.add(canvas.getCanvas());
                    }
                }
            }
        }
        if (canvases.isEmpty() && rdfResource.getResourceById(PCDM_HAS_FILE_PREDICATE) != null) {
            CanvasWithInfo canvasWithInfo = generateCanvas(request, rdfResource, 0);
            if (canvasWithInfo.getCanvasInfo().isPresent() && canvasWithInfo.getCanvasInfo().get().has("page_count")) {
                int pageCount = canvasWithInfo.getCanvasInfo().get().at("/page_count").asInt();
                for (int page = 1; page <= pageCount; ++page) {
                    canvases.add(getCanvasPage(canvasWithInfo.getCanvas(), page));
                }
            } else if (canvasWithInfo.getCanvas().getImages().size() > 0) {
                canvases.add(canvasWithInfo.getCanvas());
            }
        }

        return canvases;
    }

    private void generateOrderedCanvases(ManifestRequest request, RdfOrderedResource rdfOrderedSequence, List<Canvas> canvases) throws IOException, URISyntaxException {

        Model model = getFedoraRdfModel(rdfOrderedSequence.getResource().getURI());

        Optional<String> id = findObject(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = findObject(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {

            Model orderedModel = getFedoraRdfModel(id.get());

            CanvasWithInfo canvasWithInfo = generateCanvas(request, new RdfResource(orderedModel, id.get()), 0);
            if (canvasWithInfo.getCanvas().getImages().size() > 0) {
                canvases.add(canvasWithInfo.getCanvas());
            }

            Optional<String> nextId = findObject(model, IANA_NEXT_PREDICATE);

            if (nextId.isPresent()) {
                Resource resource = rdfOrderedSequence.getModel().getResource(nextId.get());
                rdfOrderedSequence.setResource(resource);
                rdfOrderedSequence.setCurrentId(nextId.get());
                generateOrderedCanvases(request, rdfOrderedSequence, canvases);
            }
        }

    }

    private RdfCanvas getFedoraRdfCanvas(ManifestRequest request, RdfResource rdfResource, int page) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String uri = rdfResource.getResource().getURI();
        RdfCanvas rdfCanvas = new RdfCanvas();
        String parameterizedCanvasId = RdfModelUtility.getParameterizedId(uri, request);

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
                    OptionalImageWithInfo imageWithInfo = generateImage(request, fileRdfResource, parameterizedCanvasId, page);
                    if (imageWithInfo.isPresent()) {
                        rdfCanvas.addImage(imageWithInfo.get());

                        Optional<ImageResource> imageResource = Optional.ofNullable(imageWithInfo.get().getImage().getResource());

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
