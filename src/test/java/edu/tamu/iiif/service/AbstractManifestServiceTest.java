package edu.tamu.iiif.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.repo.RedisManifestRepo;

@RunWith(SpringRunner.class)
public abstract class AbstractManifestServiceTest implements ManifestServiceTest {

    protected static final String IIIF_SERVICE_URL = "http://localhost:9000";

    protected static final String IMAGE_SERVICE_URL = "http://localhost:8182/iiif/2";

    protected static final String LOGO_URL = "https://brandguide.tamu.edu/assets/downloads/logos/TAM-Logo.png";

    @Spy
    protected ObjectMapper objectMapper;

    @Mock
    protected RestTemplate restTemplate;

    @Mock
    protected RedisManifestRepo redisManifestRepo;

    @Mock
    protected ResourceResolver resourceResolver;

    @Before
    public void init() throws URISyntaxException, NotFoundException {
        initMocks(this);
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.empty());

        when(resourceResolver.lookup(any(String.class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String url = (String) args[0];
                String prefixedContext = getRepoRdfIdentifier() + ":" + url.replace(getRepoBaseUrl(), "");
                byte[] encodedContext = Base64.getEncoder().encode(prefixedContext.getBytes());
                String id = new String(encodedContext);
                return id;
            }
        });
    }

    protected void setup(AbstractManifestService manifestService) {
        setField(manifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        setField(manifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        setField(manifestService, "logoUrl", LOGO_URL);
    }

    protected abstract String getRepoRdfIdentifier();

    protected abstract String getRepoBaseUrl();

}
