package edu.tamu.iiif.service;

import static edu.tamu.iiif.constants.Constants.*;
import static edu.tamu.iiif.constants.Constants.IIIF_IMAGE_API_CONTEXT;
import static edu.tamu.iiif.constants.Constants.IIIF_IMAGE_API_LEVEL_ZERO_PROFILE;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Service;
import de.digitalcollections.iiif.presentation.model.impl.jackson.v2.IiifPresentationApiObjectMapper;
import de.digitalcollections.iiif.presentation.model.impl.v2.MetadataImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ServiceImpl;

import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import edu.tamu.iiif.utility.StringUtility;

public abstract class AbstractManifestService implements ManifestService {

    private final static Logger LOG = LoggerFactory.getLogger(AbstractManifestService.class);

    protected final static ObjectMapper mapper = new IiifPresentationApiObjectMapper();

    @Value("${iiif.service.url}")
    protected String iiifServiceUrl;

    @Value("${iiif.image.server.url}")
    protected String imageServerUrl;

    @Value("${iiif.logo.url}")
    protected String logoUrl;

    @Autowired
    protected HttpService httpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisManifestRepo redisManifestRepo;

    @PostConstruct
    private void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    public String getManifest(String path, boolean update) throws IOException, URISyntaxException {

        String manifest;
        Optional<RedisManifest> optionalRedisManifest = getRedisManifest(path);

        if (optionalRedisManifest.isPresent()) {
            LOG.info("Manifest already in redis: " + optionalRedisManifest.get().getId() + " (" + optionalRedisManifest.get().getCreation() + ")");
            manifest = optionalRedisManifest.get().getJson();
        } else {
            LOG.info("Generating new manifest.");
            manifest = generateManifest(path);
            redisManifestRepo.save(new RedisManifest(StringUtility.encode(path), getManifestType(), getRepositoryType(), manifest));
            update = false;
        }

        if (update) {
            RedisManifest redisManifest = optionalRedisManifest.get();
            manifest = generateManifest(path);
            redisManifest.setJson(manifest);
            redisManifestRepo.save(redisManifest);
            LOG.info("Manifest update requested: " + path);
        } else {
            LOG.info("Manifest requested: " + path);
        }

        return manifest;
    }

    protected URI buildId(String path) throws URISyntaxException {
        return new URI(getIiifServiceUrl() + "/" + getManifestType().getName() + "?" + CONTEXT_IDENTIFIER + "=" + path);
    }

    protected Optional<String> getIdByPredicate(Model model, String predicate) {
        Optional<String> id = Optional.empty();
        NodeIterator firstNodeItr = model.listObjectsOfProperty(model.getProperty(predicate));
        while (firstNodeItr.hasNext()) {
            id = Optional.of(firstNodeItr.next().toString());
        }
        return id;
    }

    protected Optional<String> getLicense(RdfResource rdfResource) {
        return Optional.empty();
    }

    protected JsonNode getImageInfo(String url) throws JsonProcessingException, MalformedURLException, IOException, URISyntaxException {
        return objectMapper.readTree(fetchImageInfo(url));
    }

    protected URI getImageUri(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(url)));
    }

    protected URI getImageFullUrl(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(url), "full/full/0/default.jpg"));
    }

    protected URI getImageThumbnailUrl(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(url), "full/!200,200/0/default.jpg"));
    }

    protected URI getImageInfoUri(String url) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(url), "info.json"));
    }

    protected URI serviceUrlToThumbnailUri(URI serviceUrl) throws URISyntaxException {
        return URI.create(joinPath(serviceUrl.toString(), "full/!200,200/0/default.jpg"));
    }

    protected List<Service> getServices(RdfResource rdfResource, String... names) throws URISyntaxException {
        List<Service> services = new ArrayList<Service>();
        for (String name : names) {
            services.add(getService(rdfResource, name));
        }
        return services;
    }

    protected Service getService(RdfResource rdfResource, String name) throws URISyntaxException {
        Service service = new ServiceImpl(getImageUri(rdfResource.getResource().getURI()));
        service.setLabel(new PropertyValueSimpleImpl(name));
        service.setContext(IIIF_IMAGE_API_CONTEXT);
        service.setProfile(IIIF_IMAGE_API_LEVEL_ZERO_PROFILE);
        return service;
    }

    protected String fetchImageInfo(String url) {
        return httpService.get(url);
    }

    protected String pathIdentifier(String url) {
        return StringUtility.encode(getRepositoryPath(url));
    }

    protected String extractLabel(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }

    protected List<Metadata> getDublinCoreTermsMetadata(RdfResource rdfResource) {
        return getMetadata(rdfResource, DUBLIN_CORE_TERMS_PREFIX);
    }

    protected List<Metadata> getDublinCoreMetadata(RdfResource rdfResource) {
        return getMetadata(rdfResource, DUBLIN_CORE_PREFIX);
    }

    protected List<Metadata> getMetadata(RdfResource rdfResource, String prefix) {
        List<Metadata> metadata = new ArrayList<Metadata>();
        StmtIterator statements = rdfResource.getModel().listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();
            Property predicate = statement.getPredicate();
            String resourceUrl = rdfResource.getResource().getURI();
            String statementUrl = statement.getSubject().getURI();
            boolean match = resourceUrl.equals(statementUrl);
            if (getRepositoryType().equals(RepositoryType.DSPACE)) {
                match = getHandle(resourceUrl).equals(getHandle(statementUrl));
            } else {
                match = resourceUrl.equals(statementUrl);
            }
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

    protected String getHandle(String uri) {
        String handle;
        if (uri.contains("/handle/")) {
            handle = uri.split("/handle/")[1];
        } else if (uri.contains("/bitstream/")) {
            handle = uri.split("/bitstream/")[1];
        } else {
            handle = uri.split("/resource/")[1];
        }
        return handle;
    }

    protected String formalize(String name) {
        StringBuilder formalNameBuilder = new StringBuilder();
        name = name.replace("/", "_").trim();
        Iterator<String> parts = Arrays.asList(name.split("_")).iterator();
        boolean formalizing = true;
        while (formalizing) {
            String part = parts.next();
            if (!part.isEmpty()) {
                if (part.length() == 1) {
                    formalNameBuilder.append(part.toUpperCase());
                } else {
                    formalNameBuilder.append(part.substring(0, 1).toUpperCase());
                    formalNameBuilder.append(part.substring(1, part.length()));
                }
            }

            if (parts.hasNext()) {
                formalNameBuilder.append(" ");
            } else {
                formalizing = false;
            }
        }
        return formalNameBuilder.toString();
    }

    protected String getLogo(RdfResource rdfResource) {
        return logoUrl;
    }

    protected abstract String generateManifest(String handle) throws URISyntaxException, IOException;

    protected abstract String getIiifServiceUrl();

    protected abstract RepositoryType getRepositoryType();

    protected abstract ManifestType getManifestType();

    protected abstract String getRepositoryPath(String url);

    private Optional<RedisManifest> getRedisManifest(String path) {
        return redisManifestRepo.findByPathAndTypeAndRepository(StringUtility.encode(path), getManifestType(), getRepositoryType());
    }

    private Metadata generateMetadatum(Statement statement) throws IOException {
        Property predicate = statement.getPredicate();
        RDFNode object = statement.getObject();
        if (object instanceof Resource) {
            throw new IOException("RDF statement object is a resource, not a literal value!");
        }
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(predicate.getLocalName()));
        PropertyValueSimpleImpl value = new PropertyValueSimpleImpl(object.toString());
        return new MetadataImpl(label, value);
    }

}
