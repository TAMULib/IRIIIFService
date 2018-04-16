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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.beans.factory.annotation.Value;

import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Service;
import de.digitalcollections.iiif.presentation.model.impl.v2.MetadataImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ServiceImpl;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

public abstract class AbstractFedoraManifestService extends AbstractManifestService {

    @Value("${iiif.fedora.url}")
    protected String fedoraUrl;

    @Value("${iiif.pcdm.rdf.ext.url}")
    private String pcdmRdfExtUrl;

    protected RdfResource getRdfResource(String path) {
        String fedoraRdfUri = getId(path);
        String rdf = getRdf(fedoraRdfUri);
        System.out.println("\n" + rdf + "\n");
        Model model = generateRdfModel(rdf);
        // model.write(System.out, "JSON-LD");
        // model.write(System.out, "RDF/XML");
        return new RdfResource(model, model.getResource(fedoraRdfUri));
    }

    protected Model getRdfModel(String uri) {
        String resStr = httpService.get(uri + FEDORA_FCR_METADATA);
        return generateRdfModel(resStr);
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

    protected List<Metadata> getDublinCoreMetadata(RdfResource rdfResource) throws IOException {
        return getMetadata(rdfResource, DUBLIN_CORE_PREFIX);
    }

    protected List<Metadata> getMetadata(RdfResource rdfResource, String prefix) throws IOException {
        List<Metadata> metadata = new ArrayList<Metadata>();
        StmtIterator statements = rdfResource.getModel().listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();
            Property predicate = statement.getPredicate();
            if (rdfResource.getResource().getURI().equals(statement.getSubject().getURI()) && predicate.getNameSpace().equals(prefix)) {
                metadata.add(generateMetadatum(statement));
            }
        }
        return metadata;
    }

    protected URI getFedoraIIIFCollectionUri(String id) throws URISyntaxException {
        return getFedoraIIIFUri(id, COLLECECTION_IDENTIFIER);
    }

    protected URI getFedoraIIIFPresentationUri(String id) throws URISyntaxException {
        return getFedoraIIIFUri(id, PRESENTATION_IDENTIFIER);
    }

    protected URI getFedoraIIIFImageUri(String id) throws URISyntaxException {
        return getFedoraIIIFUri(id, IMAGE_IDENTIFIER);
    }

    private URI getFedoraIIIFUri(String id, String type) throws URISyntaxException {
        return URI.create(id.replace(fedoraUrl + "/", getIiifServiceUrl() + "/" + type + "?path="));
    }

    protected URI getImageUri(String id) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(id)));
    }

    protected URI getImageFullUrl(String id) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(id), "full/full/0/default.jpg"));
    }

    protected URI getImageThumbnailUrl(String id) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(id), "full/!200,200/0/default.jpg"));
    }

    protected URI getImageInfoUri(String id) throws URISyntaxException {
        return URI.create(joinPath(imageServerUrl, pathIdentifier(id), "info.json"));
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

}
