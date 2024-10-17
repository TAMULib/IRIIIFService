package edu.tamu.iiif.service;

import static edu.tamu.iiif.constants.Constants.IIIF_IMAGE_API_CONTEXT;
import static edu.tamu.iiif.constants.Constants.IIIF_IMAGE_API_LEVEL_ZERO_PROFILE;
import static edu.tamu.iiif.utility.RdfModelUtility.createRdfModel;
import static edu.tamu.iiif.utility.RdfModelUtility.getObjects;
import static edu.tamu.iiif.utility.StringUtility.encode;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.PropertyValue;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Service;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.jackson.v2.IiifPresentationApiObjectMapper;
import de.digitalcollections.iiif.presentation.model.impl.v2.CanvasImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ImageResourceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.MetadataImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ServiceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ThumbnailImpl;
import edu.tamu.iiif.config.model.AbstractIiifConfig;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.OptionalImageResourceWithInfo;
import edu.tamu.iiif.model.OptionalImageWithInfo;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import edu.tamu.iiif.utility.StringUtility;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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

        // This deprecated use cannot be removed until IiifPresentationApiObjectMapper() exposes builder().
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

    protected RdfResource getRdfResourceByContextPath(String contextPath) throws IOException {
        String rdfUrl = getRdfUrl(contextPath);
        Model model = getRdfModel(rdfUrl);
        return getRdfResource(model, rdfUrl);
    }

    protected RdfResource getRdfResourceByUrl(String rdfUrl) throws IOException {
        Model model = getRdfModel(rdfUrl);
        return getRdfResource(model, rdfUrl);
    }

    private RdfResource getRdfResource(Model model, String rdfUrl) {
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(rdfUrl));
    }

    protected Model getRdfModel(String url) throws IOException {
        return createRdfModel(getRdf(url));
    }

    private String getRdf(String url) throws IOException {
        logger.debug("Requesting RDF for {}", url);
        
        URL urlObject = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
        con.setRequestMethod("GET");
        return "Stub";

        /*try {
            String rdf = restTemplate.getForObject(url, String.class);
            logger.debug("RDF for {}: \n{}\n", url, rdf);

            return rdf;
        } catch (RestClientException e) {
            throw new NotFoundException("RDF not found for " + url, e);
        }*/
    }

    protected URI buildId(String path) throws URISyntaxException {
        return new URI(StringUtility.encodeSpaces(getIiifServiceUrl() + FORWARD_SLASH + getManifestType().getName() + FORWARD_SLASH + path));
    }

    protected String getLogo(RdfResource rdfResource) {
        return logoUrl;
    }

    protected OptionalImageWithInfo generateImage(ManifestRequest request, RdfResource rdfResource, String canvasId, int page) throws URISyntaxException, URISyntaxException {
        String url = rdfResource.getResource().getURI();
        OptionalImageResourceWithInfo imageResource = generateImageResource(request, rdfResource, page);
        if (imageResource.isPresent()) {
            Image image = new ImageImpl(getImageInfoUri(url));
            image.setResource(imageResource.get());
            image.setOn(getCanvasUri(canvasId));
            return OptionalImageWithInfo.of(Optional.of(image), imageResource.getImageResourceInfo());
        }
        return OptionalImageWithInfo.of(Optional.empty());
    }

    protected OptionalImageResourceWithInfo generateImageResource(ManifestRequest request, RdfResource rdfResource, int page) throws URISyntaxException {
        String url = rdfResource.getResource().getURI();

        Optional<String> optionalMimeType = getMimeType(url);

        boolean include = optionalMimeType.isPresent() ? includeResource(request, optionalMimeType.get()) : false;

        if (include) {
            logger.info("Including: " + url);
            URI infoUri = getImageInfoUri(url);

            Optional<JsonNode> imageInfoNode = getImageInfo(infoUri.toString());

            if (imageInfoNode.isPresent()) {
                ImageResource imageResource = new ImageResourceImpl(getImageFullUri(url));

                imageResource.setFormat(optionalMimeType.get());

                imageResource.setHeight(imageInfoNode.get().get(HEIGHT).asInt());

                imageResource.setWidth(imageInfoNode.get().get(WIDTH).asInt());

                imageResource.setServices(getServices(rdfResource, getIiifImageServiceName()));

                return OptionalImageResourceWithInfo.of(Optional.of(imageResource), imageInfoNode);
            } else {
                logger.info("Unable to get image info: " + infoUri.toString());
            }
        } else {
            logger.info("Excluding: " + url);
        }

        return OptionalImageResourceWithInfo.of(Optional.empty());
    }

    protected boolean includeResourceWithUrl(ManifestRequest request, String url) {
        boolean include = false;

        Optional<String> optionalMimeType = getMimeType(url);

        if (optionalMimeType.isPresent()) {
            include = includeResource(request, optionalMimeType.get());
        } else {
            logger.warn("Unable to get mime type: " + url);
        }

        return include;
    }

    protected boolean includeResource(ManifestRequest request, String mimeType) {
        boolean include = false;

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

        return include;
    }

    protected String fetchImageInfo(String url) throws NotFoundException {
        logger.debug("Fetching image info {}", url);

        try {
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            throw new NotFoundException("Image not found for " + url, e);
        }
    }

    protected URI getImageUri(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, getResourceId(url)));
    }

    protected URI getImageFullUri(String url) throws URISyntaxException {
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

    protected Canvas getCanvasPage(Canvas canvas, int page) {
        String id = canvas.getId().toString() + "?page=" + page;
        PropertyValue label = new PropertyValueSimpleImpl(canvas.getLabel().getFirstValue() + "?page=" + page);
        Canvas canvasPage = new CanvasImpl(id, label, canvas.getHeight(), canvas.getWidth());
        canvasPage.setImages(canvas.getImages().stream().map(i -> {
            Image image = new ImageImpl(i.getId().toString().replace("/info.json", ";" + page + "/info.json"));
            ImageResource ir = i.getResource();
            ImageResource imageResource = new ImageResourceImpl(ir.getId().toString().replace("/full/full/0/default.jpg", ";" + page + "/full/full/0/default.jpg"));
            imageResource.setFormat(ir.getFormat());
            imageResource.setHeight(ir.getHeight());
            imageResource.setWidth(ir.getWidth());
            List<Service> services = ir.getServices().stream().map(s -> {
                Service service = new ServiceImpl(s.getId().toString() + ";" + page);
                service.setLabel(s.getLabel());
                service.setContext(s.getContext());
                service.setProfile(s.getProfile());
                return service;
            }).collect(Collectors.toList());
            imageResource.setServices(services);
            image.setResource(imageResource);
            image.setOn(i.getOn());
            return image;
        }).collect(Collectors.toList()));
        return canvasPage;
    }

    private String getResourceId(String url) throws URISyntaxException {
        String id;
        try {
            id = resourceResolver.lookup(url);
        } catch (NotFoundException e) {
            id = resourceResolver.create(url);
        }
        return id;
    }

    protected PropertyValueSimpleImpl getLabel(RdfResource rdfResource) {
        List<String> labels = getObjects(rdfResource, getConfig().getLabelPredicates());
        if (labels.isEmpty()) {
            String id = rdfResource.getResource().getURI();
            return new PropertyValueSimpleImpl(getRepositoryContextIdentifier(id));
        }
        return new PropertyValueSimpleImpl(labels);
    }

    protected Optional<PropertyValueSimpleImpl> getDescription(RdfResource rdfResource) {
        List<String> descriptions = getObjects(rdfResource, getConfig().getDescriptionPredicates());
        return descriptions.isEmpty() ? Optional.empty() : Optional.of(new PropertyValueSimpleImpl(descriptions));
    }

    protected Optional<PropertyValueSimpleImpl> getAttribution(RdfResource rdfResource) {
        List<String> attributions = getObjects(rdfResource, getConfig().getAttributionPredicates());
        return attributions.isEmpty() ? Optional.empty() : Optional.of(new PropertyValueSimpleImpl(attributions));
    }

    protected Optional<String> getLicense(RdfResource rdfResource) {
        for (String licensePredicate : getConfig().getLicensePrecedence()) {
            List<String> licenses = getObjects(rdfResource, licensePredicate);
            if (licenses.isEmpty()) {
                continue;
            }
            return Optional.of(licenses.get(0));
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

    private Collection<Metadata> getMetadata(RdfResource rdfResource, String prefix) {
        Map<String, Metadata> metadata = new HashMap<>();
        StmtIterator statements = rdfResource.getModel().listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();
            Property predicate = statement.getPredicate();
            if (getConfig().getMetadataExclusion().contains(predicate.toString())) {
                continue;
            }
            String resourceUrl = rdfResource.getResource().getURI();
            String statementUrl = statement.getSubject().getURI();
            boolean match = getMatcherHandle(resourceUrl).equals(getMatcherHandle(statementUrl));
            if (match && predicate.getNameSpace().equals(prefix)) {
                try {
                    Metadata metadatum = generateMetadatum(statement);
                    String label = metadatum.getLabel().getFirstValue();
                    if (metadata.containsKey(label)) {
                        Metadata repeatedMedatum = metadata.get(label);
                        metadata.put(label, merge(repeatedMedatum, metadatum));
                    } else {
                        metadata.put(label, metadatum);
                    }
                } catch (IOException e) {
                    logger.warn("Unable to generate metadatum for {}", statement);
                }
            }
        }
        return metadata.values();
    }

    private Metadata merge(Metadata m1, Metadata m2) {
        List<String> values = new ArrayList<>();
        values.addAll(m1.getValue().getValues());
        values.addAll(m2.getValue().getValues());
        return new MetadataImpl(m1.getLabel(), new PropertyValueSimpleImpl(values));
    }

    private Metadata generateMetadatum(Statement statement) throws IOException {
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();
        if (object instanceof Resource) {
            throw new IOException("RDF statement object is a resource, not a literal value!");
        }
        return buildMetadata(predicate.getLocalName(), object.toString());
    }

    protected Optional<JsonNode> getImageInfo(String url) {
        Optional<JsonNode> imageInfoNode = Optional.empty();
        try {
            imageInfoNode = Optional.of(objectMapper.readTree(fetchImageInfo(url)));
        } catch (IOException e) {
            logger.info("Unable to get image info: " + url);
            logger.warn(e.getMessage());
        }
        return imageInfoNode;
    }

    protected Optional<String> getMimeType(String url) {
        try {
            HttpHeaders headers = restTemplate.headForHeaders(url);
            return Optional.ofNullable(headers.getFirst(HttpHeaders.CONTENT_TYPE));
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    private Metadata buildMetadata(String label, String value) {
        return new MetadataImpl(new PropertyValueSimpleImpl(label), new PropertyValueSimpleImpl(value));
    }

}
