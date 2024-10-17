package edu.tamu.iiif.service;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractManifestServiceTest {

    protected static final String JSON_DIR = "json/";

    protected static final String IMAGE_DIR = "image/";

    protected static final String IIIF_SERVICE_URL = "http://localhost:9000";

    protected static final String IMAGE_SERVICE_URL = "http://localhost:8182/iiif/2";

    protected static final String MIMETYPE_TURTLE = "text/turtle;charset=utf-8";

    protected static final String LOGO_URL = "https://library.tamu.edu/assets/images/tamu-logos/TAM-PrimaryMarkB.png";

    protected static final String RDF_DIR = "rdf/";

    protected static final String SIMULATE_FAILURE = "Simulate Failure";

    @Spy
    protected ObjectMapper objectMapper;

    protected RestTemplate restTemplate;

    @Mock
    protected RedisManifestRepo redisManifestRepo;

    @Mock
    protected ResourceResolver resourceResolver;

    //protected MockRestServiceServer mockServer;

    //private RestGatewaySupport mockGateway;

    @BeforeEach
    public void init() throws URISyntaxException, NotFoundException {
        restTemplate = new RestTemplateBuilder().build();
        //mockGateway = new RestGatewaySupport();
        //mockGateway.setRestTemplate(restTemplate);
        //mockServer = MockRestServiceServer.createServer(mockGateway);

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
     * Get the mock files such that the implementer can specify custom directory paths.
     *
     * This is intended to be called relative to the classpath such as via the `@Value` Spring annotation.
     *
     * @return The directory file path with a trailing forward slash.
     */
    protected String getMockDirectoryPath() {
        return "mock/";
    }

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
