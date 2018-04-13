package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.rdf.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.DUBLIN_CORE_PREFIX;
import static edu.tamu.iiif.constants.rdf.Constants.DUBLIN_CORE_TITLE_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.EBUCORE_HAS_MIME_TYPE_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.EBUCORE_HEIGHT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.EBUCORE_WIDTH_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.FEDORA_FCR_METADATA;
import static edu.tamu.iiif.constants.rdf.Constants.FEDORA_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.IIIF_IMAGE_API_CONTEXT;
import static edu.tamu.iiif.constants.rdf.Constants.IIIF_IMAGE_API_LEVEL_ZERO_PROFILE;
import static edu.tamu.iiif.constants.rdf.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.PRESENTATION_IDENTIFIER;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Service;
import de.digitalcollections.iiif.presentation.model.impl.v2.MetadataImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ServiceImpl;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.fedora.FedoraRdfResource;
import edu.tamu.iiif.service.AbstractManifestService;
import edu.tamu.iiif.utility.StringUtility;

public abstract class AbstractFedoraManifestService extends AbstractManifestService {

    @Value("${iiif.fedora.url}")
    protected String fedoraUrl;

    @Value("${iiif.pcdm.rdf.ext.url}")
    private String pcdmRdfExtUrl;

    @Autowired
    private ObjectMapper objectMapper;

    protected FedoraRdfResource getFedoraRdfResource(String path) {
        String fedoraRdfUri = getId(path);
        String rdf = getRdf(fedoraRdfUri);
        System.out.println("\n" + rdf + "\n");
        Model model = generateRdfModel(rdf);
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new FedoraRdfResource(model, model.getResource(fedoraRdfUri));
    }

    protected Model getRdfModel(String uri) {
        String resStr = httpService.get(uri + FEDORA_FCR_METADATA);
        return generateRdfModel(resStr);
    }

    protected Optional<String> getLicense(FedoraRdfResource fedoraRdfResource) {
        return Optional.empty();
    }

    protected PropertyValueSimpleImpl getTitle(FedoraRdfResource fedoraRdfResource) {
        Optional<String> title = getObject(fedoraRdfResource, DUBLIN_CORE_TITLE_PREDICATE);
        if (!title.isPresent()) {
            title = Optional.of(formalize(getFedoraPath(fedoraRdfResource.getResource().getURI())));
        }
        return new PropertyValueSimpleImpl(title.get());
    }

    protected PropertyValueSimpleImpl getDescription(FedoraRdfResource fedoraRdfResource) {
        Optional<String> description = getObject(fedoraRdfResource, DUBLIN_CORE_TITLE_PREDICATE);
        if (!description.isPresent()) {
            description = Optional.of("N/A");
        }
        return new PropertyValueSimpleImpl(description.get());
    }

    protected Optional<String> getMimeType(FedoraRdfResource fedoraRdfResource) {
        return getObject(fedoraRdfResource, EBUCORE_HAS_MIME_TYPE_PREDICATE);
    }

    protected Optional<Integer> getHeight(FedoraRdfResource fedoraRdfResource) {
        Optional<Integer> height = Optional.empty();
        Optional<String> heightAsString = getObject(fedoraRdfResource, EBUCORE_HEIGHT_PREDICATE);
        if (heightAsString.isPresent()) {
            height = Optional.of(Integer.parseInt(heightAsString.get()));
        }
        return height;
    }

    protected Optional<Integer> getWidth(FedoraRdfResource fedoraRdfResource) {
        Optional<Integer> width = Optional.empty();
        Optional<String> widthAsString = getObject(fedoraRdfResource, EBUCORE_WIDTH_PREDICATE);
        if (widthAsString.isPresent()) {
            width = Optional.of(Integer.parseInt(widthAsString.get()));
        }
        return width;
    }

    private Optional<String> getObject(FedoraRdfResource fedoraRdfResource, String uri) {
        Optional<String> metadatum = Optional.empty();
        Statement statement = fedoraRdfResource.getStatementOfPropertyWithId(uri);
        if (statement != null) {
            RDFNode object = statement.getObject();
            if (!object.toString().isEmpty()) {
                metadatum = Optional.of(object.toString());
            }
        }
        return metadatum;
    }

    protected List<Metadata> getDublinCoreMetadata(FedoraRdfResource fedoraRdfResource) throws IOException {
        return getMetadata(fedoraRdfResource, DUBLIN_CORE_PREFIX);
    }

    protected List<Metadata> getMetadata(FedoraRdfResource fedoraRdfResource, String prefix) throws IOException {
        List<Metadata> metadata = new ArrayList<Metadata>();
        StmtIterator statements = fedoraRdfResource.getModel().listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();
            Property predicate = statement.getPredicate();
            if (fedoraRdfResource.getResource().getURI().equals(statement.getSubject().getURI()) && predicate.getNameSpace().equals(prefix)) {
                metadata.add(generateMetadatum(statement));
            }
        }
        return metadata;
    }

    protected URI getFedoraIIIFCollectionUrl(String id) throws URISyntaxException {
        return getFedoraIIIFUrl(id, COLLECECTION_IDENTIFIER);
    }

    protected URI getFedoraIIIFPresentationUrl(String id) throws URISyntaxException {
        return getFedoraIIIFUrl(id, PRESENTATION_IDENTIFIER);
    }

    protected URI getFedoraIIIFImageUrl(String id) throws URISyntaxException {
        return getFedoraIIIFUrl(id, IMAGE_IDENTIFIER);
    }

    private URI getFedoraIIIFUrl(String id, String type) throws URISyntaxException {
        return new URI(id.replace(fedoraUrl + "/", getIiifServiceUrl() + "/" + type + "?path="));
    }

    protected URI getImageUrl(String id) throws URISyntaxException {
        return new URI(joinPath(imageServerUrl, pathIdentifier(id)));
    }

    protected URI getImageFullUrl(String id) throws URISyntaxException {
        return new URI(joinPath(imageServerUrl, pathIdentifier(id), "full/full/0/default.jpg"));
    }

    protected URI getImageThumbnailUrl(String id) throws URISyntaxException {
        return new URI(joinPath(imageServerUrl, pathIdentifier(id), "full/!200,200/0/default.jpg"));
    }

    protected URI getImageInfoUrl(String id) throws URISyntaxException {
        return new URI(joinPath(imageServerUrl, pathIdentifier(id), "info.json"));
    }

    protected URI serviceUrlToThumbnailUrl(URI serviceUrl) throws URISyntaxException {
        return new URI(joinPath(serviceUrl.toString(), "full/!200,200/0/default.jpg"));
    }

    protected JsonNode getImageInfo(String url) throws JsonProcessingException, MalformedURLException, IOException, URISyntaxException {
        return objectMapper.readTree(fetchImageInfo(url));
    }

    protected String fetchImageInfo(String url) {
        return httpService.get(url);
    }

    protected String extractLabel(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
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

    protected List<Service> getServices(FedoraRdfResource fedoraRdfResource, String... names) throws URISyntaxException {
        List<Service> services = new ArrayList<Service>();
        for (String name : names) {
            services.add(getService(fedoraRdfResource, name));
        }
        return services;
    }

    protected Service getService(FedoraRdfResource fedoraRdfResource, String name) throws URISyntaxException {
        Service service = new ServiceImpl(getImageUrl(fedoraRdfResource.getResource().getURI()));
        service.setLabel(new PropertyValueSimpleImpl(name));
        service.setContext(IIIF_IMAGE_API_CONTEXT);
        service.setProfile(IIIF_IMAGE_API_LEVEL_ZERO_PROFILE);
        return service;
    }

    protected String getLogo(FedoraRdfResource fedoraRdfResource) {
        return logoUrl;
    }

    @Override
    protected String getIiifServiceUrl() {
        return iiifServiceUrl + "/" + FEDORA_IDENTIFIER;
    }

    @Override
    protected RepositoryType getRepositoryType() {
        return FEDORA;
    }

    private String getId(String path) {
        return joinPath(fedoraUrl, path);
    }

    private String getRdf(String fedoraPath) {
        return httpService.get(pcdmRdfExtUrl, fedoraPath);
    }

    private Model generateRdfModel(String rdf) {
        InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "TTL");
        return model;
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

    private String pathIdentifier(String url) {
        return StringUtility.encode(getFedoraPath(url));
    }

    private String getFedoraPath(String url) {
        return FEDORA_IDENTIFIER + ":" + url.substring(fedoraUrl.length() + 1);
    }

}
