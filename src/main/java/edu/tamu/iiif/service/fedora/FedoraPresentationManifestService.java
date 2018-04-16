package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.rdf.Constants.FEDORA_HAS_PARENT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.IANA_LAST_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.IANA_NEXT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.LDP_CONTAINS_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.ORE_PROXY_FOR_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.PCDM_HAS_FILE_PREDICATE;
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
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfOrderedSequence;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraPresentationManifestService extends AbstractFedoraManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        RdfResource rdfResource = getRdfResource(path);

        URI id = buildId(path);

        PropertyValueSimpleImpl label = getTitle(rdfResource);

        Manifest manifest = new ManifestImpl(id, label);

        manifest.setDescription(getDescription(rdfResource));

        manifest.setMetadata(getDublinCoreMetadata(rdfResource));

        List<Sequence> sequences = getSequences(rdfResource);

        manifest.setSequences(sequences);

        manifest.setLogo(getLogo(rdfResource));

        Optional<Thumbnail> thumbnail = getThumbnail(sequences);
        if (thumbnail.isPresent()) {
            manifest.setThumbnail(thumbnail.get());
        }

        Optional<String> license = getLicense(rdfResource);
        if (license.isPresent()) {
            manifest.setLicense(license.get());
        }

        return mapper.writeValueAsString(manifest);
    }

    private List<Sequence> getSequences(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Sequence> sequences = new ArrayList<Sequence>();

        sequences.add(generateSequence(rdfResource));

        return sequences;
    }

    private Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
        Sequence sequence = new SequenceImpl(getFedoraIIIFPresentationUri(id), label);
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

    private Canvas generateCanvas(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));

        RdfCanvas rdfCanvas = getFedoraRdfCanvas(rdfResource);

        Canvas canvas = new CanvasImpl(getFedoraIIIFPresentationUri(id), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

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
        image.setOn(getFedoraIIIFPresentationUri(canvasId));
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

    private Optional<Thumbnail> getThumbnail(List<Sequence> sequences) throws URISyntaxException {
        for (Sequence sequence : sequences) {
            for (Canvas canvas : sequence.getCanvases()) {
                for (Image image : canvas.getImages()) {
                    if (Optional.ofNullable(image.getResource()).isPresent()) {
                        URI serviceURI = image.getResource().getServices().get(0).getId();
                        Thumbnail thubmnail = new ThumbnailImpl(serviceUrlToThumbnailUri(serviceURI));
                        thubmnail.setServices(image.getResource().getServices());
                        return Optional.of(thubmnail);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected ManifestType getManifestType() {
        return PRESENTATION;
    }

}
