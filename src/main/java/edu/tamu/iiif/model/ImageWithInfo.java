package edu.tamu.iiif.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Image;

public class ImageWithInfo {

  private final Image image;

  private final Optional<JsonNode> imageInfo;

  private ImageWithInfo(Image image, Optional<JsonNode> imageInfo) {
    this.image = image;
    this.imageInfo = imageInfo;
  }

  public Image getImage() {
    return image;
  }

  public Optional<JsonNode> getImageInfo() {
    return imageInfo;
  }

  public static ImageWithInfo of(Image image) {
    return ImageWithInfo.of(image, Optional.empty());
  }

  public static ImageWithInfo of(Image image, Optional<JsonNode> imageInfo) {
    return new ImageWithInfo(image, imageInfo);
  }

}
