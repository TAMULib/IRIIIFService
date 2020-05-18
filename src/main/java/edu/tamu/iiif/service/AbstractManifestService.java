package edu.tamu.iiif.service;

import static edu.tamu.iiif.constants.Constants.IIIF_IMAGE_API_CONTEXT;
import static edu.tamu.iiif.constants.Constants.IIIF_IMAGE_API_LEVEL_ZERO_PROFILE;
import static edu.tamu.iiif.utility.RdfModelUtility.createRdfModel;
import static edu.tamu.iiif.utility.RdfModelUtility.getObject;
import static edu.tamu.iiif.utility.StringUtility.encode;
import static edu.tamu.iiif.utility.StringUtility.encodeSpaces;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Service;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.jackson.v2.IiifPresentationApiObjectMapper;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.MetadataImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ServiceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ThumbnailImpl;
import edu.tamu.iiif.config.model.AbstractIiifConfig;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.model.repo.RedisManifestRepo;

public abstract class AbstractManifestService implements ManifestService {

    private final static Logger logger = LoggerFactory.getLogger(AbstractManifestService.class);

    private final static String SEMI_COLON = ";";
    private final static String FORWARD_SLASH = "/";

    private final static String IIIF_THUMBNAIL_PATH = "full/!100,100/0/default.jpg";
    private final static String IIIF_FULL_PATH = "full/full/0/default.jpg";

    private final static String APPLICATION_PDF = "application/pdf";

    private final static String IMAGE_JSON = "info.json";

    private final static String IMAGE = "image";

    private final static String HEIGHT = "height";
    private final static String WIDTH = "width";

    private final static String CONTEXT_LABEL = "context";

    protected final static ObjectMapper mapper = new IiifPresentationApiObjectMapper();

    @Value("${iiif.service.url}")
    protected String iiifServiceUrl;

    @Value("${iiif.image.server.url}")
    protected String imageServerUrl;

    @Value("${iiif.logo.url}")
    protected String logoUrl;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisManifestRepo redisManifestRepo;

    @Autowired
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    public String getManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String path = request.getContext();
        boolean update = request.isUpdate();
        String manifest;
        Optional<RedisManifest> optionalRedisManifest = getRedisManifest(request);

        if (optionalRedisManifest.isPresent()) {
            logger.info("Manifest already in redis: " + optionalRedisManifest.get().getId() + " (" + optionalRedisManifest.get().getCreation() + ")");
            manifest = optionalRedisManifest.get().getJson();
        } else {
            logger.info("Generating new manifest.");
            manifest = generateManifest(request);
            redisManifestRepo.save(new RedisManifest(encode(path), getManifestType(), getRepository(), request.getAllowed(), request.getDisallowed(), manifest));
            update = false;
        }

        if (update) {
            RedisManifest redisManifest = optionalRedisManifest.get();
            manifest = generateManifest(request);
            redisManifest.setJson(manifest);
            redisManifestRepo.save(redisManifest);
            logger.info("Manifest update requested: " + path);
        } else {
            logger.info("Manifest requested: " + path);
        }

        return manifest;
    }

    protected RdfResource getRdfResourceByContextPath(String contextPath) throws NotFoundException {
        String rdfUrl = getRdfUrl(contextPath);
        Model model = getRdfModel(rdfUrl);
        return getRdfResource(model, rdfUrl);
    }

    protected RdfResource getRdfResourceByUrl(String rdfUrl) throws NotFoundException {
        Model model = getRdfModel(rdfUrl);
        return getRdfResource(model, rdfUrl);
    }

    private RdfResource getRdfResource(Model model, String rdfUrl) {
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(rdfUrl));
    }

    protected Model getRdfModel(String url) throws NotFoundException {
        return createRdfModel(getRdf(url));
    }

    protected String getRdf(String url) throws NotFoundException {
        Optional<String> rdf = Optional.ofNullable(restTemplate.getForObject(url, String.class));
        if (rdf.isPresent()) {
            return rdf.get();
        }
        throw new NotFoundException("RDF not found! " + url);
    }

    protected URI buildId(String path) throws URISyntaxException {
        return new URI(encodeSpaces(getIiifServiceUrl() + FORWARD_SLASH + getManifestType().getName() + FORWARD_SLASH + path));
    }

    protected String getLogo(RdfResource rdfResource) {
        return logoUrl;
    }

    protected Optional<Image> generateImage(ManifestRequest request, RdfResource rdfResource, String canvasId) throws URISyntaxException, URISyntaxException {
        String url = rdfResource.getResource().getURI();
        Optional<Image> optionalImage = Optional.empty();
        Optional<ImageResource> imageResource = generateImageResource(request, rdfResource);
        if (imageResource.isPresent()) {
            Image image = new ImageImpl(getImageInfoUri(url));
            image.setResource(imageResource.get());
            image.setOn(getCanvasUri(canvasId));
            optionalImage = Optional.of(image);
        }
        return optionalImage;
    }

    protected Optional<ImageResource> generateImageResource(ManifestRequest request, RdfResource rdfResource) throws URISyntaxException {
        String url = rdfResource.getResource().getURI();

        Optional<ImageResource> optionalImageResource = Optional.empty();

        Optional<String> optionalMimeType = getMimeType(url);

        boolean include = false;

        if (optionalMimeType.isPresent()) {

            String mimeType = optionalMimeType.get();

            logger.debug("Mime type: " + mimeType);

            if (mimeType.contains(SEMI_COLON)) {
                mimeType = mimeType.split(SEMI_COLON)[0];
            }

            include = mimeType.startsWith(IMAGE) || mimeType.equals(APPLICATION_PDF);
            if (include) {
                String allowed = request.getAllowed();
                if (allowed.length() > 0) {
                    logger.debug("Allowed: " + allowed);
                    include = allowed.contains(mimeType);
                } else {
                    String disallowed = request.getDisallowed();
                    if (disallowed.length() > 0) {
                        logger.debug("Disallowed: " + disallowed);
                        include = !disallowed.contains(mimeType);
                    }
                }
            }
        } else {
            logger.warn("Unable to get mime type: " + url);
        }

        if (include) {
            logger.info("Including: " + url);
            URI infoUri = getImageInfoUri(url);

            Optional<JsonNode> imageInfoNode = getImageInfo(infoUri.toString());

            if (imageInfoNode.isPresent()) {

                ImageResource imageResource = new ImageResourceImpl(getImageFullUrl(url));

                imageResource.setFormat(optionalMimeType.get());

                imageResource.setHeight(imageInfoNode.get().get(HEIGHT).asInt());

                imageResource.setWidth(imageInfoNode.get().get(WIDTH).asInt());

                imageResource.setServices(getServices(rdfResource, getIiifImageServiceName()));

                optionalImageResource = Optional.of(imageResource);
            } else {
                logger.info("Unable to get image info: " + infoUri.toString());
            }
        } else {
            logger.info("Excluding: " + url);
        }

        return optionalImageResource;
    }

    protected String fetchImageInfo(String url) throws NotFoundException {
        Optional<String> imageInfo = Optional.ofNullable(restTemplate.getForObject(url, String.class));
        if (imageInfo.isPresent()) {
            return imageInfo.get();
        }
        throw new NotFoundException("Image information not found!");
    }

    protected URI getImageUri(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, getResourceId(url)));
    }

    protected URI getImageFullUrl(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, getResourceId(url), IIIF_FULL_PATH));
    }

    protected URI getImageThumbnailUrl(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, getResourceId(url), IIIF_THUMBNAIL_PATH));
    }

    protected URI getImageInfoUri(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, getResourceId(url), IMAGE_JSON));
    }

    protected URI serviceUrlToThumbnailUri(URI serviceUrl) throws URISyntaxException {
        return URI.create(joinPath(serviceUrl.toString(), IIIF_THUMBNAIL_PATH));
    }

    protected List<Service> getServices(RdfResource rdfResource, String... names) throws URISyntaxException {
        List<Service> services = new ArrayList<Service>();
        for (String name : names) {
            services.add(getService(rdfResource, name));
        }
        return services;
    }

    protected Optional<Thumbnail> getThumbnail(List<Sequence> sequences) throws URISyntaxException {
        Optional<Thumbnail> optionalThumbnail = Optional.empty();
        exit: for (Sequence sequence : sequences) {
            for (Canvas canvas : sequence.getCanvases()) {
                for (Image image : canvas.getImages()) {
                    if (Optional.ofNullable(image.getResource()).isPresent()) {
                        URI serviceURI = image.getResource().getServices().get(0).getId();
                        Thumbnail thubmnail = new ThumbnailImpl(serviceUrlToThumbnailUri(serviceURI));
                        thubmnail.setServices(image.getResource().getServices());
                        optionalThumbnail = Optional.of(thubmnail);
                        continue exit;
                    }
                }
            }
        }
        return optionalThumbnail;
    }

    private String getResourceId(String url) throws URISyntaxException {
        try {
            return resourceResolver.lookup(url);
        } catch (NotFoundException e) {
            return resourceResolver.create(url);
        }
    }

    protected PropertyValueSimpleImpl getLabel(RdfResource rdfResource) {
        Optional<String> title = Optional.empty();
        for (String labelPredicate : getConfig().getLabelPrecedence()) {
            title = getObject(rdfResource, labelPredicate);
            if (title.isPresent()) {
                return new PropertyValueSimpleImpl(title.get());
            }
        }
        String id = rdfResource.getResource().getURI();
        return new PropertyValueSimpleImpl(getRepositoryContextIdentifier(id));
    }

    protected Optional<PropertyValueSimpleImpl> getDescription(RdfResource rdfResource) {
        Optional<String> description = Optional.empty();
        for (String descriptionPredicate : getConfig().getDescriptionPrecedence()) {
            description = getObject(rdfResource, descriptionPredicate);
            if (description.isPresent()) {
                return Optional.of(new PropertyValueSimpleImpl(description.get()));
            }
        }
        return Optional.empty();
    }

    protected Optional<PropertyValueSimpleImpl> getAttribution(RdfResource rdfResource) {
        Optional<String> attribution = Optional.empty();
        for (String attributionPredicate : getConfig().getAttributionPrecedence()) {
            attribution = getObject(rdfResource, attributionPredicate);
            if (attribution.isPresent()) {
                return Optional.of(new PropertyValueSimpleImpl(attribution.get()));
            }
        }
        return Optional.empty();
    }

    protected Optional<String> getLicense(RdfResource rdfResource) {
        Optional<String> license = Optional.empty();
        for (String licensePredicate : getConfig().getLicensePrecedence()) {
            license = getObject(rdfResource, licensePredicate);
            if (license.isPresent()) {
                return Optional.of(license.get());
            }
        }
        return Optional.empty();
    }

    protected List<Metadata> getMetadata(RdfResource rdfResource) {
        List<Metadata> metadata = new ArrayList<Metadata>();
        for (String metadataPrefix : getConfig().getMetadataPrefixes()) {
            metadata.addAll(getMetadata(rdfResource, metadataPrefix));
        }
        if (getConfig().getContextAsMetadata()) {
            metadata.add(buildMetadata(CONTEXT_LABEL, getRepositoryPath(rdfResource.getId())));
        }
        return metadata;
    }

    protected abstract String getRdfUrl(String context);

    protected abstract String getMatcherHandle(String url);

    protected abstract String generateManifest(ManifestRequest request) throws URISyntaxException, IOException;

    protected abstract String getIiifServiceUrl();

    protected abstract URI getCanvasUri(String canvasId) throws URISyntaxException;

    protected abstract String getIiifImageServiceName();

    protected abstract String getRepositoryContextIdentifier(String url);

    protected abstract String getRepositoryPath(String url);

    protected abstract AbstractIiifConfig getConfig();

    private Service getService(RdfResource rdfResource, String name) throws URISyntaxException {
        Service service = new ServiceImpl(getImageUri(rdfResource.getResource().getURI()));
        service.setLabel(new PropertyValueSimpleImpl(name));
        service.setContext(IIIF_IMAGE_API_CONTEXT);
        service.setProfile(IIIF_IMAGE_API_LEVEL_ZERO_PROFILE);
        return service;
    }

    private Optional<RedisManifest> getRedisManifest(ManifestRequest request) {
        return redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(encode(request.getContext()), getManifestType(), getRepository(), request.getAllowed(), request.getDisallowed());
    }

    private List<Metadata> getMetadata(RdfResource rdfResource, String prefix) {
        List<Metadata> metadata = new ArrayList<Metadata>();
        StmtIterator statements = rdfResource.getModel().listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();
            Property predicate = statement.getPredicate();
            String resourceUrl = rdfResource.getResource().getURI();
            String statementUrl = statement.getSubject().getURI();
            boolean match = getMatcherHandle(resourceUrl).equals(getMatcherHandle(statementUrl));
            if (match && predicate.getNameSpace().equals(prefix)) {
                Optional<Metadata> metadatum = Optional.empty();
                try {
                    metadatum = Optional.of(generateMetadatum(statement));
                } catch (IOException e) {

                }
                if (metadatum.isPresent()) {
                    metadata.add(metadatum.get());
                }
            }
        }
        return metadata;
    }

    private Metadata generateMetadatum(Statement statement) throws IOException {
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();
        if (object instanceof Resource) {
            throw new IOException("RDF statement object is a resource, not a literal value!");
        }
        return buildMetadata(predicate.getLocalName(), object.toString());
    }

    private Optional<JsonNode> getImageInfo(String url) {
        Optional<JsonNode> imageInfoNode = Optional.empty();
        try {
            imageInfoNode = Optional.of(objectMapper.readTree(fetchImageInfo(url)));
        } catch (IOException e) {
            logger.info("Unable to get image info: " + url);
            logger.warn(e.getMessage());
        }
        return imageInfoNode;
    }

    private Optional<String> getMimeType(String url) {
        HttpHeaders headers = restTemplate.headForHeaders(url);
        return Optional.ofNullable(headers.getFirst(HttpHeaders.CONTENT_TYPE));
    }

    private Metadata buildMetadata(String label, String value) {
        return new MetadataImpl(new PropertyValueSimpleImpl(label), new PropertyValueSimpleImpl(value));
    }

}
