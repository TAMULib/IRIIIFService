package edu.tamu.iiif.model;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;

public class CanvasWithInfo {

  private final Canvas canvas;

  private final Optional<JsonNode> canvasInfo;

  private CanvasWithInfo(Canvas canvas, Optional<JsonNode> canvasInfo) {
    this.canvas = canvas;
    this.canvasInfo = canvasInfo;
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public Optional<JsonNode> getCanvasInfo() {
    return canvasInfo;
  }

  public static CanvasWithInfo of(Canvas canvas) {
    return CanvasWithInfo.of(canvas, Optional.empty());
  }

  public static CanvasWithInfo of(Canvas canvas, Optional<JsonNode> canvasInfo) {
    return new CanvasWithInfo(canvas, canvasInfo);
  }

}
