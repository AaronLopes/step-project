package com.google.sps.data;

/** A class to hold map marker data*/
public final class Marker {

  private final long id;
  private final double latitude;
  private final double longitude;
  private final String title;
  private final String description;

  public Marker(long id, double latitude, double longitude, String title, String description) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.title = title;
    this.description = description;
  }
}