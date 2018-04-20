package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TITLE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.EBUCORE_HAS_MIME_TYPE_PREDICATE;
import static edu.tamu.iiif.constants.Constants.EBUCORE_HEIGHT_PREDICATE;
import static edu.tamu.iiif.constants.Constants.EBUCORE_WIDTH_PREDICATE;
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
import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
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

    protected RdfResource getRdfResource(String path) {
        String fedoraRdfUri = getFedoraUrl(path);
        String rdf = getPCDMRdf(fedoraRdfUri);
        Model model = generateRdfModel(rdf);
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(fedoraRdfUri));
    }

    protected Model getRdfModel(String uri) {
        String resStr = httpService.get(uri + FEDORA_FCR_METADATA);
        return generateRdfModel(resStr);
    }

    protected Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
        Sequence sequence = new SequenceImpl(getFedoraIIIFSequenceUri(id), label);
        sequence.setCanvases(getCanvases(rdfResource));
        return sequence;
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

    protected Canvas generateCanvas(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));

        RdfCanvas rdfCanvas = getFedoraRdfCanvas(rdfResource);

        Canvas canvas = new CanvasImpl(getFedoraIIIFCanvasUri(id), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        canvas.setMetadata(getDublinCoreMetadata(rdfResource));

        return canvas;
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

                int height = image.getResource().getHeight();
                if (height > rdfCanvas.getHeight()) {
                    rdfCanvas.setHeight(height);
                }

                int width = image.getResource().getWidth();
                if (width > rdfCanvas.getWidth()) {
                    rdfCanvas.setWidth(width);
                }
            }
        }
        return rdfCanvas;
    }

    private Image generateImage(RdfResource rdfResource, String canvasId) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String id = rdfResource.getResource().getURI();
        Image image = new ImageImpl(getImageInfoUri(id));
        image.setResource(generateImageResource(rdfResource));
        image.setOn(getFedoraIIIFCanvasUri(canvasId));
        return image;
    }

    private ImageResource generateImageResource(RdfResource rdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String id = rdfResource.getResource().getURI();
        ImageResource imageResource = new ImageResourceImpl(getImageFullUrl(id));

        URI infoUri = getImageInfoUri(id);

        JsonNode imageInfoNode = getImageInfo(infoUri.toString());

        Optional<String> mimeType = getMimeType(rdfResource);
        if (mimeType.isPresent()) {
            imageResource.setFormat(mimeType.get());
        }

        imageResource.setHeight(imageInfoNode.get("height").asInt());

        imageResource.setWidth(imageInfoNode.get("width").asInt());

        imageResource.setServices(getServices(rdfResource, "Fedora IIIF Image Resource Service"));

        return imageResource;
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

    protected Optional<String> getMimeType(RdfResource rdfResource) {
        return getObject(rdfResource, EBUCORE_HAS_MIME_TYPE_PREDICATE);
    }

    protected Optional<Integer> getHeight(RdfResource rdfResource) {
        Optional<Integer> height = Optional.empty();
        Optional<String> heightAsString = getObject(rdfResource, EBUCORE_HEIGHT_PREDICATE);
        if (heightAsString.isPresent()) {
            height = Optional.of(Integer.parseInt(heightAsString.get()));
        }
        return height;
    }

    protected Optional<Integer> getWidth(RdfResource rdfResource) {
        Optional<Integer> width = Optional.empty();
        Optional<String> widthAsString = getObject(rdfResource, EBUCORE_WIDTH_PREDICATE);
        if (widthAsString.isPresent()) {
            width = Optional.of(Integer.parseInt(widthAsString.get()));
        }
        return width;
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

    protected URI getFedoraIIIFCollectionUri(String url) throws URISyntaxException {
        return getFedoraIIIFUri(url, COLLECECTION_IDENTIFIER);
    }

    protected URI getFedoraIIIFPresentationUri(String url) throws URISyntaxException {
        return getFedoraIIIFUri(url, PRESENTATION_IDENTIFIER);
    }

    protected URI getFedoraIIIFSequenceUri(String url) throws URISyntaxException {
        return getFedoraIIIFUri(url, SEQUENCE_IDENTIFIER);
    }

    protected URI getFedoraIIIFCanvasUri(String url) throws URISyntaxException {
        return getFedoraIIIFUri(url, CANVAS_IDENTIFIER);
    }

    protected URI getFedoraIIIFImageUri(String url) throws URISyntaxException {
        return getFedoraIIIFUri(url, IMAGE_IDENTIFIER);
    }

    private URI getFedoraIIIFUri(String url, String type) throws URISyntaxException {
        return URI.create(url.replace(fedoraUrl + "/", getIiifServiceUrl() + "/" + type + "?path="));
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

    private String getPCDMRdf(String fedoraPath) {
        return httpService.get(pcdmRdfExtUrl, fedoraPath);
    }

    private Model generateRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
    }

}
