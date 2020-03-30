package edu.tamu.iiif.controller;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.service.ResourceResolver;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceResolver resourceResolver;

    @GetMapping(value = "/{id}", produces = "text/plain")
    public ResponseEntity<String> getResourceUrl(@PathVariable String id) throws NotFoundException {
        return ResponseEntity.ok(resourceResolver.resolve(id));
    }

    @GetMapping(value = "/{id}/redirect")
    public RedirectView redirectToResource(@PathVariable String id) throws NotFoundException {
        String url = resourceResolver.resolve(id);
        RedirectView redirect = new RedirectView(url);
        redirect.setStatusCode(MOVED_PERMANENTLY);
        return redirect;
    }

    @GetMapping(value = "/lookup", produces = "text/plain")
    public ResponseEntity<String> getResourceId(@RequestParam(value = "uri", required = true) String uri) throws URISyntaxException, NotFoundException {
        return ResponseEntity.ok(resourceResolver.lookup(uri));
    }

    @RequestMapping(method = { POST, PUT }, produces = "text/plain")
    public ResponseEntity<String> addResource(@RequestParam(value = "uri", required = true) String uri) throws URISyntaxException {
        try {
            return new ResponseEntity<>(resourceResolver.lookup(uri), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(resourceResolver.create(uri), HttpStatus.CREATED);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}", produces = "text/plain")
    public void removeResource(@PathVariable String id) throws NotFoundException {
        resourceResolver.remove(id);
    }

}
