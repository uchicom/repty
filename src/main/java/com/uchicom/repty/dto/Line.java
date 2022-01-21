// (C) 2018 uchicom
package com.uchicom.repty.dto;

import java.util.Arrays;

/**
 * 罫線情報.
 *
 * @author shigeki.uchiyama
 */
public class Line {

  public Line(String colorKey, float width) {
    this.colorKey = colorKey;
    this.width = width;
  }

  public Line(String colorKey, float width, float phase) {
    this(colorKey, width);
    this.phase = phase;
  }

  public Line(String colorKey, float width, float[] pattern, float phase) {
    this(colorKey, width, phase);
    this.pattern = pattern;
  }

  public String getColorKey() {
    return colorKey;
  }

  public void setColorKey(String colorKey) {
    this.colorKey = colorKey;
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  String colorKey;
  float width;
  float[] pattern;
  float phase;

  public float[] getPattern() {
    return pattern;
  }

  public void setPattern(float[] pattern) {
    this.pattern = pattern;
  }

  public float getPhase() {
    return phase;
  }

  public void setPhase(float phase) {
    this.phase = phase;
  }

  @Override
  public String toString() {
    return "Line [colorKey="
        + colorKey
        + ", width="
        + width
        + ", pattern="
        + Arrays.toString(pattern)
        + ", phase="
        + phase
        + "]";
  }
}
