package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.CONTEXT_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TITLE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.FEDORA_FCR_METADATA;
import static edu.tamu.iiif.constants.Constants.FEDORA_HAS_PARENT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.FEDORA_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.LDP_CONTAINS_PREDICATE;
import static edu.tamu.iiif.constants.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PCDM_HAS_FILE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static edu.tamu.iiif.model.RepositoryType.FEDORA;
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
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfOrderedSequence;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

@Profile(FEDORA_IDENTIFIER)
public abstract class AbstractFedoraManifestService extends AbstractManifestService {

    @Value("${iiif.fedora.url}")
    protected String fedoraUrl;

    @Value("${iiif.pcdm.rdf.ext.url}")
    private String pcdmRdfExtUrl;

    protected RdfResource getRdfResource(String path) throws NotFoundException {
        String fedoraRdfUri = getFedoraUrl(path);
        String rdf = getPCDMRdf(fedoraRdfUri);
        Model model = generateRdfModel(rdf);
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(fedoraRdfUri));
    }

    protected Model getRdfModel(String uri) throws NotFoundException {
        Optional<String> fedoraRdf = Optional.ofNullable(httpService.get(uri + FEDORA_FCR_METADATA));
        if (fedoraRdf.isPresent()) {
            return generateRdfModel(fedoraRdf.get());
        }
        throw new NotFoundException("Fedora RDF not found!");
    }

    protected Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
        Sequence sequence = new SequenceImpl(getFedoraIiifSequenceUri(id), label);
        sequence.setCanvases(getCanvases(rdfResource));
        return sequence;
    }

    protected Canvas generateCanvas(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));

        RdfCanvas rdfCanvas = getFedoraRdfCanvas(rdfResource);

        Canvas canvas = new CanvasImpl(getFedoraIiifCanvasUri(id), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        canvas.setMetadata(getDublinCoreMetadata(rdfResource));

        return canvas;
    }

    protected PropertyValueSimpleImpl getTitle(RdfResource rdfResource) {
        Optional<String> title = getObject(rdfResource, DUBLIN_CORE_TITLE_PREDICATE);
        if (!title.isPresent()) {
            title = Optional.of(formalize(getRepositoryPath(rdfResource.getResource().getURI())));
        }
        return new PropertyValueSimpleImpl(title.get());
    }

    protected PropertyValueSimpleImpl getDescription(RdfResource rdfResource) {
        Optional<String> description = getObject(rdfResource, DUBLIN_CORE_TITLE_PREDICATE);
        if (!description.isPresent()) {
            description = Optional.of("N/A");
        }
        return new PropertyValueSimpleImpl(description.get());
    }

    private Optional<String> getObject(RdfResource rdfResource, String uri) {
        Optional<String> metadatum = Optional.empty();
        Statement statement = rdfResource.getStatementOfPropertyWithId(uri);
        if (statement != null) {
            RDFNode object = statement.getObject();
            if (!object.toString().isEmpty()) {
                metadatum = Optional.of(object.toString());
            }
        }
        return metadatum;
    }

    protected URI getFedoraIiifCollectionUri(String url) throws URISyntaxException {
        return getFedoraIiifUri(url, COLLECECTION_IDENTIFIER);
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

    protected URI getFedoraIiifImageUri(String url) throws URISyntaxException {
        return getFedoraIiifUri(url, IMAGE_IDENTIFIER);
    }

    private URI getFedoraIiifUri(String url, String type) throws URISyntaxException {
        return URI.create(url.replace(fedoraUrl + "/", getIiifServiceUrl() + "/" + type + "?" + CONTEXT_IDENTIFIER + "="));
    }

    @Override
    protected String getIiifServiceUrl() {
        return iiifServiceUrl + "/" + FEDORA_IDENTIFIER;
    }

    @Override
    protected String getRepositoryPath(String url) {
        return FEDORA_IDENTIFIER + ":" + url.substring(fedoraUrl.length() + 1);
    }

    @Override
    protected RepositoryType getRepositoryType() {
        return FEDORA;
    }

    private String getFedoraUrl(String path) {
        return joinPath(fedoraUrl, path);
    }

    private String getPCDMRdf(String fedoraPath) throws NotFoundException {
        Optional<String> fedoraRdf = Optional.ofNullable(httpService.get(pcdmRdfExtUrl, fedoraPath));
        if (fedoraRdf.isPresent()) {
            return fedoraRdf.get();
        }
        throw new NotFoundException("Fedora PCDM RDF not found!");
    }

    private Model generateRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

    private List<Canvas> getCanvases(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();

        Optional<String> firstId = getIdByPredicate(rdfResource.getModel(), IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = getIdByPredicate(rdfResource.getModel(), IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = rdfResource.getModel().getResource(firstId.get());
                generateOrderedCanvases(new RdfOrderedSequence(rdfResource.getModel(), firstResource, firstId.get(), lastId.get()), canvases);
            }
        }

        if (canvases.isEmpty()) {

            ResIterator resItr = rdfResource.listResourcesWithPropertyWithId(LDP_CONTAINS_PREDICATE);
            while (resItr.hasNext()) {
                Resource resource = resItr.next();
                if (resource.getProperty(rdfResource.getProperty(PCDM_HAS_FILE_PREDICATE)) != null) {
                    canvases.add(generateCanvas(new RdfResource(rdfResource, resource)));
                }
            }

        }

        return canvases;
    }

    private void generateOrderedCanvases(RdfOrderedSequence rdfOrderedSequence, List<Canvas> canvases) throws IOException, URISyntaxException {

        Model model = getRdfModel(rdfOrderedSequence.getResource().getURI());

        Optional<String> id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {

            if (!rdfOrderedSequence.isLast()) {

                canvases.add(generateCanvas(new RdfResource(rdfOrderedSequence, rdfOrderedSequence.getModel().getResource(id.get()))));

                Optional<String> nextId = getIdByPredicate(model, IANA_NEXT_PREDICATE);

                if (nextId.isPresent()) {
                    Resource resource = rdfOrderedSequence.getModel().getResource(nextId.get());
                    rdfOrderedSequence.setResource(resource);
                    rdfOrderedSequence.setCurrentId(nextId.get());
                    generateOrderedCanvases(rdfOrderedSequence, canvases);
                }
            }
        }

    }

    private RdfCanvas getFedoraRdfCanvas(RdfResource rdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        RdfCanvas rdfCanvas = new RdfCanvas();

        String canvasId = rdfResource.getResource().getURI();

        Statement canvasStatement = rdfResource.getStatementOfPropertyWithId(LDP_CONTAINS_PREDICATE);

        String parentId = canvasStatement.getObject().toString();

        for (Resource resource : rdfResource.listResourcesWithPropertyWithId(FEDORA_HAS_PARENT_PREDICATE).toList()) {

            if (resource.getProperty(rdfResource.getProperty(FEDORA_HAS_PARENT_PREDICATE)).getObject().toString().equals(parentId)) {

                RdfResource fileFedoraRdfResource = new RdfResource(rdfResource, resource.getURI());

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
            }
        }
        return rdfCanvas;
    }

    protected URI getCanvasUri(String canvasId) throws URISyntaxException {
        return getFedoraIiifCanvasUri(canvasId);
    }

    protected String getIiifImageServiceName() {
        return "Fedora IIIF Image Resource Service";
    }

}
