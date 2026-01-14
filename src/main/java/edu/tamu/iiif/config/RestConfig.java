package edu.tamu.iiif.config;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.requestFactory(() -> new SimpleClientHttpRequestFactory() {
                    @Override
                    protected void prepareConnection(HttpURLConnection connection,
                                                     String httpMethod) throws IOException {
                        super.prepareConnection(connection, httpMethod);
                        // always follow redirect for any request method
                        connection.setInstanceFollowRedirects(true);
                    }
                }).build();
    }

}
