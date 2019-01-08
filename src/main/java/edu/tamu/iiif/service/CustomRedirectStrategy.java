package edu.tamu.iiif.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class CustomRedirectStrategy extends DefaultRedirectStrategy {

    private final static UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" }, UrlValidator.ALLOW_LOCAL_URLS);

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        if (isRedirect(response)) {
            Optional<Header> locationHeader = Optional.ofNullable(response.getFirstHeader("location"));
            if (locationHeader.isPresent()) {
                try {
                    URI origUri = new URI(request.getRequestLine().getUri());
                    String location = locationHeader.get().getValue().split("\\?")[0];
                    if (urlValidator.isValid(location)) {
                        return new URI(location);
                    } else {
                        return new URI(origUri.getScheme(), null, origUri.getHost(), origUri.getPort(), location, null, null);
                    }
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