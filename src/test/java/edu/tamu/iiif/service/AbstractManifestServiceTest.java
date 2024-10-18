package edu.tamu.iiif.service;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.repo.RedisManifestRepo;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.mock.Expectation;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@MockServerSettings(ports = { 8182, 9000 })
@ExtendWith(MockitoExtension.class)
@ExtendWith(MockServerExtension.class)
@ExtendWith(SpringExtension.class)
public abstract class AbstractManifestServiceTest {

    protected static final String JSON_DIR = "json/";

    protected static final String IMAGE_DIR = "image/";

    protected static final String IIIF_SERVICE_URL = "http://localhost:9000";

    protected static final String IMAGE_SERVICE_URL_PATH = "/iiif/2";

    protected static final String IMAGE_SERVICE_URL = "http://localhost:8182" + IMAGE_SERVICE_URL_PATH;

    protected static final String MIMETYPE_TURTLE = "text/turtle;charset=utf-8";

    protected static final String LOGO_URL = "https://library.tamu.edu/assets/images/tamu-logos/TAM-PrimaryMarkB.png";

    protected static final String RDF_DIR = "rdf/";

    protected static final String SIMULATE_FAILURE = "Simulate Failure";

    protected ClientAndServer client;

    @Spy
    protected ObjectMapper objectMapper;

    @Mock
    protected RestTemplate restTemplate;

    @Mock
    protected RedisManifestRepo redisManifestRepo;

    @Mock
    protected ResourceResolver resourceResolver;

    @BeforeEach
    public void init(ClientAndServer client) throws URISyntaxException, NotFoundException {
        this.client = client;

        lenient().when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.empty());

        lenient().when(resourceResolver.lookup(any(String.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String url = (String) args[0];
                String prefixedContext = getRepoRdfIdentifier() + ":" + url.replace(getRepoBaseUrl(), "");
                byte[] encodedContext = Base64.getEncoder().encode(prefixedContext.getBytes());
                return new String(encodedContext);
            }
        });
    }

    /**
     * Helper for mocking a successfull REST response for a standard RDF.
     *
     * @param path The path to process.
     * @param resource The resource file representing the RDF to return.
     *
     * @return The standard response from the mock server respond function.
     *
     * @throws IOException An I/O exception.
     */
    protected Expectation[] restGetRdfSuccess(String path, Resource resource) throws IOException {
        return client.when(request().withMethod("GET")
            .withPath(path)
        )
        .respond(response().withStatusCode(200)
            .withHeaders(header("Content-Type", MIMETYPE_TURTLE))
            .withBody(loadResource(resource))
        );
    }

    /**
     * Helper for mocking a successfull REST response for a standard image response.
     *
     * @param path The path to process.
     * @param resource The resource file representing the RDF to return.
     *
     * @return The standard response from the mock server respond function.
     *
     * @throws IOException An I/O exception.
     */
    protected Expectation[] restGetImageSuccess(String path, String mime) throws IOException {
        return client.when(request().withMethod("GET")
            .withPath(path)
        )
        .respond(response().withStatusCode(200)
            .withHeaders(header("Content-Type", mime))
            .withBody("fake image data")
        );
    }

    /**
     * Helper for mocking a Bad Request REST response for a standard RDF.
     *
     * @param path The path to process.
     *
     * @return The standard response from the mock server respond function.
     *
     * @throws IOException An I/O exception.
     */
    protected Expectation[] restGetRdfBadRequest(String path) throws IOException {
        return client.when(request().withMethod("GET")
            .withPath(path)
        )
        .respond(response().withStatusCode(400));
    }

    /**
     * Get the mock files such that the implementer can specify custom directory paths.
     *
     * This is intended to be called relative to the classpath such as via the `@Value` Spring annotation.
     *
     * @return The directory file path with a trailing forward slash.
     */
    protected String getMockDirectoryPath() {
        return "mock/";
    }

    /**
     * Setup the manifest URLs and other settings.
     *
     * @param manifestService The manifest service.
     */
    protected void setup(AbstractManifestService manifestService) {
        setField(manifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        setField(manifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        setField(manifestService, "logoUrl", LOGO_URL);
    }

    /**
     * Load the resources as a string
     *
     * @param resource The resource to load.
     *
     * @return A string representing the contents of the resource.
     *
     * @throws IOException On failure to load the resource.
     */
    protected String loadResource(Resource resource) throws IOException {
        return readFileToString(resource.getFile(), "UTF-8");
    }

    protected abstract String getRepoRdfIdentifier();

    protected abstract String getRepoBaseUrl();

    /**
     * Get the path to the mock file.
     *
     * @param dir The sub-directory the file is stored within (must add trailing forward slash).
     * @param file The name of the file.
     *
     * @return The path to the file.
     */
    abstract protected String getMockFilePath(String type, String file);

    /**
     * Setup the mocks.
     *
     * @throws IOException
     */
    abstract protected void setupMocks() throws IOException;

}
