package edu.tamu.iiif.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class CustomRedirectStrategy extends DefaultRedirectStrategy {

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