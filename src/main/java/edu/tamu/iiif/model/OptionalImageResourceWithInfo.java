package edu.tamu.iiif.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.ImageResource;

public class OptionalImageResourceWithInfo {

  private final Optional<ImageResource> imageResource;

  private final Optional<JsonNode> imageResourceInfo;

  private OptionalImageResourceWithInfo(Optional<ImageResource> imageResource, Optional<JsonNode> imageResourceInfo) {
    this.imageResource = imageResource;
    this.imageResourceInfo = imageResourceInfo;
  }

  public Optional<ImageResource> getImageResource() {
    return imageResource;
  }

  public Optional<JsonNode> getImageResourceInfo() {
    return imageResourceInfo;
  }

  public boolean isPresent() {
    return imageResource.isPresent();
  }

  public ImageResource get() {
    return imageResource.get();
  }

  public static OptionalImageResourceWithInfo of(Optional<ImageResource> imageResource) {
    return OptionalImageResourceWithInfo.of(imageResource, Optional.empty());
  }

  public static OptionalImageResourceWithInfo of(Optional<ImageResource> imageResource, Optional<JsonNode> imageResourceInfo) {
    return new OptionalImageResourceWithInfo(imageResource, imageResourceInfo);
  }

}
