package edu.tamu.iiif.service;

import static edu.tamu.iiif.utility.StringUtility.encodeSpaces;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HttpService {

    private final static Logger LOG = LoggerFactory.getLogger(HttpService.class);

    private final static List<NameValuePair> EMPTY_PARAMETERS = new ArrayList<NameValuePair>();

    @Value("${iiif.service.connection.timeout:60000}")
    private int connectionTimeout;

    @Value("${iiif.service.connection.timeToLive:60000}")
    private int connectionTimeToLive;

    @Value("${iiif.service.connection.request.timeout:30000}")
    private int connectionRequestTimeout;

    @Value("${iiif.service.socket.timeout:60000}")
    private int socketTimeout;

    private static PoolingHttpClientConnectionManager connectionManager;

    private static CloseableHttpClient httpClient;

    @PostConstruct
    private void init() throws URISyntaxException {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
        connectionManager = new PoolingHttpClientConnectionManager();
        httpClient = HttpClients.custom().setRedirectStrategy(new CustomRedirectStrategy()).setConnectionManager(connectionManager).setConnectionTimeToLive(connectionTimeToLive, TimeUnit.MILLISECONDS).setDefaultRequestConfig(config).build();
    }

    @PreDestroy
    private void cleanUp() throws IOException {
        httpClient.close();
        connectionManager.close();
    }

    public String get(String url) {
        LOG.debug("GET: " + url);
        return get(url, EMPTY_PARAMETERS);
    }

    public String get(String url, String context) {
        LOG.debug("GET: " + url.concat("?context=").concat(context));
        return get(url, Arrays.asList(new BasicNameValuePair("context", context)));
    }

    public String contentType(String url) {
        LOG.debug("HEAD: " + url);
        String mimeType = null;
        try (CloseableHttpResponse response = request(craftHead(url, EMPTY_PARAMETERS))) {
            Optional<Header> contentType = Optional.ofNullable(response.getFirstHeader("Content-Type"));
            if (contentType.isPresent()) {
                mimeType = contentType.get().getValue();
                LOG.debug("Mime Type: " + mimeType);
            } else {
                LOG.warn("No Content-Type!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("Error performing HEAD request: " + url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            LOG.warn("Invalid URI: " + url);
        }
        return mimeType;
    }

    private String get(String url, List<NameValuePair> parameters) {
        String body = null;
        try (CloseableHttpResponse response = request(craftGet(url, parameters))) {
            body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("Error performing GET request: " + url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            LOG.warn("Invalid URI: " + url);
        }
        return body;
    }

    private CloseableHttpResponse request(HttpRequestBase request) throws IOException, URISyntaxException {
        CloseableHttpResponse response = httpClient.execute(request);
        StatusLine sl = response.getStatusLine();
        int sc = sl.getStatusCode();
        if (sc < HttpStatus.SC_OK || sc >= HttpStatus.SC_MULTIPLE_CHOICES) {
            response.close();
            throw new IOException("Incorrect response status: " + sc);
        }
        return response;
    }

    private HttpGet craftGet(String url, List<NameValuePair> parameters) throws URISyntaxException {
        return new HttpGet(buildUrl(url, parameters));
    }

    private HttpHead craftHead(String url, List<NameValuePair> parameters) throws URISyntaxException {
        return new HttpHead(buildUrl(url, parameters));
    }

    private URI buildUrl(String url, List<NameValuePair> parameters) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(encodeSpaces(url));
        builder.setParameters(parameters);
        return builder.build();
    }

    private class CustomRedirectStrategy extends DefaultRedirectStrategy {

        @Override
        public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            if (isRedirect(response)) {
                Optional<Header> locationHeader = Optional.ofNullable(response.getFirstHeader("location"));
                if (locationHeader.isPresent()) {
                    try {
                        URI origUri = new URI(request.getRequestLine().getUri());
                        String path = locationHeader.get().getValue().split("\\?")[0];
                        return new URI(origUri.getScheme(), origUri.getHost(), path, null);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException("Unable to reconstruct original URI!");
                    }
                } else {
                    throw new RuntimeException("No location header provided with redirect!");
                }
            }
            return super.getLocationURI(request, response, context);
        }

        private boolean isRedirect(HttpResponse response) {
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY || response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY;
        }

    }

}
