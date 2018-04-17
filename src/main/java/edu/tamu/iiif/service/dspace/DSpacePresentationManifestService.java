package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.NodeIterator;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Manifest;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ManifestImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ThumbnailImpl;
import edu.tamu.iiif.constants.rdf.Constants;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfCanvas;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpacePresentationManifestService extends AbstractDSpaceManifestService {

    public String generateManifest(String handle) throws IOException, URISyntaxException {

        RdfResource rdfResource = getDSpaceRdfModel(handle);

        URI id = buildId(handle);

        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);

        Manifest manifest = new ManifestImpl(id, label);

        List<Sequence> sequences = getSequences(rdfResource);

        manifest.setSequences(sequences);

        manifest.setLogo(getLogo(rdfResource));

        manifest.setMetadata(getDublinCoreTermsMetadata(rdfResource));

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
        if (hasCollections(rdfResource.getModel())) {

            NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(Constants.DSPACE_HAS_COLLECTION_PREDICATE);
            while (collectionIterator.hasNext()) {
                String uri = collectionIterator.next().toString();
                String handle = getHandle(uri);
                sequences.addAll(getSequences(getDSpaceRdfModel(handle)));
            }

        } else if (hasItems(rdfResource.getModel())) {

            NodeIterator collectionIterator = rdfResource.getAllNodesOfPropertyWithId(Constants.DSPACE_HAS_ITEM_PREDICATE);
            while (collectionIterator.hasNext()) {
                String uri = collectionIterator.next().toString();
                String handle = getHandle(uri);
                sequences.addAll(getSequences(getDSpaceRdfModel(handle)));
            }

        } else {
            if (isItem(rdfResource.getModel())) {
                sequences.add(generateSequence(rdfResource));
            }
        }
        return sequences;
    }

    private Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);
        Sequence sequence = new SequenceImpl(getDSpaceIIIFPresentationUri(handle), label);
        sequence.setCanvases(getCanvases(rdfResource));
        return sequence;
    }

    private List<Canvas> getCanvases(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();

        canvases.add(generateCanvas(rdfResource));

        return canvases;
    }

    private Canvas generateCanvas(RdfResource rdfResource) throws IOException, URISyntaxException {
        String uri = rdfResource.getResource().getURI();
        String handle = getHandle(uri);
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);

        RdfCanvas rdfCanvas = getDSpaceRdfCanvas(rdfResource);

        Canvas canvas = new CanvasImpl(getDSpaceIIIFPresentationUri(handle), label, rdfCanvas.getHeight(), rdfCanvas.getWidth());

        canvas.setImages(rdfCanvas.getImages());

        canvas.setMetadata(new ArrayList<Metadata>());

        return canvas;
    }

    private RdfCanvas getDSpaceRdfCanvas(RdfResource rdfResource) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        RdfCanvas rdfCanvas = new RdfCanvas();
        String canvasId = getHandle(rdfResource.getResource().getURI());

        NodeIterator bitstreamIterator = rdfResource.getAllNodesOfPropertyWithId(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        while (bitstreamIterator.hasNext()) {
            String uri = bitstreamIterator.next().toString();

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

        }
        return rdfCanvas;
    }

    private Image generateImage(RdfResource rdfResource, String canvasId) throws URISyntaxException, JsonProcessingException, MalformedURLException, IOException {
        String url = rdfResource.getResource().getURI();
        Image image = new ImageImpl(getImageInfoUri(url));
        image.setResource(generateImageResource(rdfResource));
        image.setOn(getDSpaceIIIFPresentationUri(canvasId));
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
