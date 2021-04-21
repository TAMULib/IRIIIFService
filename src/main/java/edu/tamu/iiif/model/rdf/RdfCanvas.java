package edu.tamu.iiif.model.rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import edu.tamu.iiif.model.ImageWithInfo;

public class RdfCanvas {

    private int height;

    private int width;

    private final List<ImageWithInfo> images;

    public RdfCanvas() {
        this.height = 0;
        this.width = 0;
        this.images = new ArrayList<ImageWithInfo>();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<Image> getImages() {
        return images.stream()
            .map(imageWithInfo -> imageWithInfo.getImage())
            .collect(Collectors.toList());
    }

    public List<Optional<JsonNode>> getImagesInfo() {
        return images.stream()
            .map(imageWithInfo -> imageWithInfo.getImageInfo())
            .collect(Collectors.toList());
    }

    public void addImage(ImageWithInfo image) {
        images.add(image);
    }

}
