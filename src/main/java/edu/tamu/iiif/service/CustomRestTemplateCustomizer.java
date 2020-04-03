package edu.tamu.iiif.service;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomRestTemplateCustomizer implements RestTemplateCustomizer {

    @Value("${iiif.service.connection.timeout:60000}")
    private int connectionTimeout;

    @Value("${iiif.service.connection.timeToLive:60000}")
    private int connectionTimeToLive;

    @Value("${iiif.service.connection.request.timeout:30000}")
    private int connectionRequestTimeout;

    @Value("${iiif.service.socket.timeout:60000}")
    private int socketTimeout;

    @Override
    public void customize(RestTemplate restTemplate) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new CustomRedirectStrategy()).setConnectionManager(connectionManager).setConnectionTimeToLive(connectionTimeToLive, TimeUnit.MILLISECONDS).setDefaultRequestConfig(config).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
    }

}