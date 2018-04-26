package edu.tamu.iiif.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HttpService {

    private final static Logger LOG = LoggerFactory.getLogger(HttpService.class);

    private final static List<NameValuePair> EMPTY_PARAMETERS = new ArrayList<NameValuePair>();

    @Value("${iiif.service.connection.timeout}")
    private int connectionTimeout;

    @Value("${iiif.service.connection.request.timeout}")
    private int connectionRequestTimeout;

    @Value("${iiif.service.socket.timeout}")
    private int socketTimeout;

    @Value("${iiif.service.request.retries}")
    private int retries;

    private CloseableHttpClient httpClient;

    @PostConstruct
    private void init() throws URISyntaxException {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    @PreDestroy
    private void cleanUp() throws IOException {
        httpClient.close();
    }

    public String get(String url) {
        LOG.debug("Request: " + url);
        return attemptGet(url, EMPTY_PARAMETERS, 0);
    }

    public String get(String url, String context) {
        LOG.debug("Request: " + url.concat("?context=").concat(context));
        return attemptGet(url, Arrays.asList(new BasicNameValuePair("context", context)), 0);
    }

    private String attemptGet(String url, List<NameValuePair> parameters, int retry) {
        String response = null;
        try {
            response = get(url, parameters);
        } catch (IOException e) {
            if (retry < retries) {
                LOG.debug("Request failed. Retry attempt " + retry + ".");
                response = attemptGet(url, parameters, ++retry);
            } else {
                LOG.warn(e.getMessage());
            }
        } catch (URISyntaxException e) {
            LOG.warn(e.getMessage());
        }
        return response;
    }

    private String get(String url, List<NameValuePair> parameters) throws IOException, URISyntaxException {
        CloseableHttpResponse response = httpClient.execute(craftRequest(url, parameters));
        try {
            StatusLine sl = response.getStatusLine();
            int sc = sl.getStatusCode();
            switch (sc) {
            case 200:
                break;
            default:
                throw new IOException("Incorrect response status: " + sc);
            }
            return EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }
    }

    private HttpGet craftRequest(String url, List<NameValuePair> parameters) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        builder.setParameters(parameters);
        return new HttpGet(builder.build());
    }

}
