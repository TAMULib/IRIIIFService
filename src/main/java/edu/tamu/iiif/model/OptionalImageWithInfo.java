package edu.tamu.iiif.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Image;

public class OptionalImageWithInfo {

  private final Optional<Image> image;

  private final Optional<JsonNode> imageInfo;

  private OptionalImageWithInfo(Optional<Image> image, Optional<JsonNode> imageInfo) {
    this.image = image;
    this.imageInfo = imageInfo;
  }

  public Optional<Image> getImage() {
    return image;
  }

  public Optional<JsonNode> getImageInfo() {
    return imageInfo;
  }

  public boolean isPresent() {
    return image.isPresent();
  }

  public ImageWithInfo get() {
    return ImageWithInfo.of(image.get(), imageInfo);
  }

  public static OptionalImageWithInfo of(Optional<Image> image) {
    return OptionalImageWithInfo.of(image, Optional.empty());
  }

  public static OptionalImageWithInfo of(Optional<Image> image, Optional<JsonNode> imageInfo) {
    return new OptionalImageWithInfo(image, imageInfo);
  }

}
