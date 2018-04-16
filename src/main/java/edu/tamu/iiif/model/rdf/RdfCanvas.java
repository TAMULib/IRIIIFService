package edu.tamu.iiif.model.rdf;

import java.util.ArrayList;
import java.util.List;

import de.digitalcollections.iiif.presentation.model.api.v2.Image;

public class RdfCanvas {

    private int height;

    private int width;

    private final List<Image> images;

    public RdfCanvas() {
        this.height = 0;
        this.width = 0;
        this.images = new ArrayList<Image>();
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
        return images;
    }

    public void addImage(Image image) {
        images.add(image);
    }

}
