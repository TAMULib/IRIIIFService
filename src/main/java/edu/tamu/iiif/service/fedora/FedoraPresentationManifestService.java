package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.FEDORA_HAS_PARENT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.LDP_CONTAINS_PREDICATE;
import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.rdf.FedoraRdfConstants.PCDM_HAS_FILE_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.PRESENTATION;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Manifest;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ManifestImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ThumbnailImpl;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.fedora.FedoraRdfCanvas;
import edu.tamu.iiif.model.rdf.fedora.FedoraRdfOrderedSequence;
import edu.tamu.iiif.model.rdf.fedora.FedoraRdfResource;

@Service
public class FedoraPresentationManifestService extends AbstractFedoraManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        FedoraRdfResource fedoraRdfResource = getFedoraRdfResource(path);

        URI id = buildId(path);

        PropertyValueSimpleImpl label = getTitle(fedoraRdfResource);

        Manifest manifest = new ManifestImpl(id, label);

        manifest.setDescription(getDescription(fedoraRdfResource));

        manifest.setMetadata(getDublinCoreMetadata(fedoraRdfResource));

        List<Sequence> sequences = getSequences(fedoraRdfResource);

        manifest.setSequences(sequences);

        manifest.setLogo(getLogo(fedoraRdfResource));

        Optional<Thumbnail> thumbnail = getThumbnail(sequences);
        if (thumbnail.isPresent()) {
            manifest.setThumbnail(thumbnail.get());
        }

        Optional<String> license = getLicense(fedoraRdfResource);
        if (license.isPresent()) {
            manifest.setLicense(license.get());
        }

        return mapper.writeValueAsString(manifest);
    }

    private List<Sequence> getSequences(FedoraRdfResource fedoraRdfResource) throws IOException, URISyntaxException {
        List<Sequence> sequences = new ArrayList<Sequence>();

        sequences.add(generateSequence(fedoraRdfResource));

        return sequences;
    }

    private Sequence generateSequence(FedoraRdfResource fedoraRdfResource) throws IOException, URISyntaxException {
        String id = fedoraRdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
        Sequence sequence = new SequenceImpl(getFedoraIIIFPresentationUrl(id), label);
        sequence.setCanvases(getCanvases(fedoraRdfResource));
        return sequence;
    }

    private Optional<Thumbnail> getThumbnail(List<Sequence> sequences) throws URISyntaxException {
        for (Sequence sequence : sequences) {
            for (Canvas canvas : sequence.getCanvases()) {
                for (Image image : canvas.getImages()) {
                    if (Optional.ofNullable(image.getResource()).isPresent()) {
                        URI serviceURI = image.getResource().getServices().get(0).getId();
                        Thumbnail thubmnail = new ThumbnailImpl(serviceUrlToThumbnailUrl(serviceURI));
                        thubmnail.setServices(image.getResource().getServices());
                        return Optional.of(thubmnail);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private List<Canvas> getCanvases(FedoraRdfResource fedoraRdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();

        Optional<String> firstId = getIdByPredicate(fedoraRdfResource.getModel(), IANA_FIRST_PREDICATE);

        if (firstId.isPresent()) {
            Optional<String> lastId = getIdByPredicate(fedoraRdfResource.getModel(), IANA_LAST_PREDICATE);

            if (lastId.isPresent()) {
                Resource firstResource = fedoraRdfResource.getModel().getResource(firstId.get());
                generateOrderedCanvases(new FedoraRdfOrderedSequence(fedoraRdfResource.getModel(), firstResource, firstId.get(), lastId.get()), canvases);
            }
        }

        if (canvases.isEmpty()) {

            ResIterator resItr = fedoraRdfResource.listResourcesWithPropertyWithId(LDP_CONTAINS_PREDICATE);
            while (resItr.hasNext()) {
                Resource resource = resItr.next();
                if (resource.getProperty(fedoraRdfResource.getProperty(PCDM_HAS_FILE_PREDICATE)) != null) {
                    canvases.add(generateCanvas(new FedoraRdfResource(fedoraRdfResource, resource)));
                }
            }

        }

        return canvases;
    }

    private void generateOrderedCanvases(FedoraRdfOrderedSequence fedoraRdfOrderedSequence, List<Canvas> canvases) throws IOException, URISyntaxException {

        Model model = getRdfModel(fedoraRdfOrderedSequence.getResource().getURI());

        Optional<String> id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE);

        if (!id.isPresent()) {
            id = getIdByPredicate(model, ORE_PROXY_FOR_PREDICATE.replace("#", "/"));
        }

        if (id.isPresent()) {

            if (!fedoraRdfOrderedSequence.isLast()) {

                canvases.add(generateCanvas(new FedoraRdfResource(fedoraRdfOrderedSequence, fedoraRdfOrderedSequence.getModel().getResource(id.get()))));

                Optional<String> nextId = getIdByPredicate(model, IANA_NEXT_PREDICATE);

                if (nextId.isPresent()) {
                    Resource resource = fedoraRdfOrderedSequence.getModel().getResource(nextId.get());
                    fedoraRdfOrderedSequence.setResource(resource);
                    fedoraRdfOrderedSequence.setCurrentId(nextId.get());
                    generateOrderedCanvases(fedoraRdfOrderedSequence, canvases);
                }
            }
        }

    }

    private Canvas generateCanvas(FedoraRdfResource fedoraRdfResource) throws IOException, URISyntaxException {
        String id = fedoraRdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));

        FedoraRdfCanvas fedoraRdfCanvas = getFedoraRdfCanvas(fedoraRdfResource);

        Canvas canvas = new CanvasImpl(getFedoraIIIFPresentationUrl(id), label, fedoraRdfCanvas.getHeight(), fedoraRdfCanvas.getWidth());

        canvas.setImages(fedoraRdfCanvas.getImages());

        canvas.setMetadata(getDublinCoreMetadata(fedoraRdfResource));

        return canvas;
    }

    private FedoraRdfCanvas getFedoraRdfCanvas(FedoraRdfResource fedoraRdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        FedoraRdfCanvas fedoraRdfCanvas = new FedoraRdfCanvas();

        String canvasId = fedoraRdfResource.getResource().getURI();

        Statement canvasStatement = fedoraRdfResource.getStatementOfPropertyWithId(LDP_CONTAINS_PREDICATE);

        String parentId = canvasStatement.getObject().toString();

        for (Resource resource : fedoraRdfResource.listResourcesWithPropertyWithId(FEDORA_HAS_PARENT_PREDICATE).toList()) {

            if (resource.getProperty(fedoraRdfResource.getProperty(FEDORA_HAS_PARENT_PREDICATE)).getObject().toString().equals(parentId)) {

                FedoraRdfResource fileFedoraRdfResource = new FedoraRdfResource(fedoraRdfResource, resource.getURI());

                Optional<String> mimetype = getMimeType(fileFedoraRdfResource);

                if (mimetype.isPresent() && mimetype.get().startsWith("image")) {

                    Image image = generateImage(fileFedoraRdfResource, canvasId);
                    fedoraRdfCanvas.addImage(image);

                    int height = image.getResource().getHeight();
                    if (height > fedoraRdfCanvas.getHeight()) {
                        fedoraRdfCanvas.setHeight(height);
                    }

                    int width = image.getResource().getWidth();
                    if (width > fedoraRdfCanvas.getWidth()) {
                        fedoraRdfCanvas.setWidth(width);
                    }
                }
            }
        }
        return fedoraRdfCanvas;
    }

    private Image generateImage(FedoraRdfResource fedoraRdfResource, String canvasId) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String id = fedoraRdfResource.getResource().getURI();
        Image image = new ImageImpl(getImageInfoUrl(id));
        image.setResource(generateImageResource(fedoraRdfResource));
        image.setOn(getFedoraIIIFPresentationUrl(canvasId));
        return image;
    }

    private ImageResource generateImageResource(FedoraRdfResource fedoraRdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String id = fedoraRdfResource.getResource().getURI();
        ImageResource imageResource = new ImageResourceImpl(getImageFullUrl(id));

        URI infoUri = getImageInfoUrl(id);

        JsonNode imageInfoNode = getImageInfo(infoUri.toString());

        Optional<String> mimeType = getMimeType(fedoraRdfResource);
        if (mimeType.isPresent()) {
            imageResource.setFormat(mimeType.get());
        }

        imageResource.setHeight(imageInfoNode.get("height").asInt());

        imageResource.setWidth(imageInfoNode.get("width").asInt());

        imageResource.setServices(getServices(fedoraRdfResource, "Fedora IIIF Image Resource Service"));

        return imageResource;
    }

    @Override
    protected ManifestType getManifestType() {
        return PRESENTATION;
    }

}
