package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.constants.rdf.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.DUBLIN_CORE_TITLE_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.EBUCORE_HAS_MIME_TYPE_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.EBUCORE_HEIGHT_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.EBUCORE_WIDTH_PREDICATE;
import static edu.tamu.iiif.constants.rdf.Constants.FEDORA_FCR_METADATA;
import static edu.tamu.iiif.constants.rdf.Constants.FEDORA_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.model.RepositoryType.FEDORA;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.springframework.beans.factory.annotation.Value;

import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.service.AbstractManifestService;

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
