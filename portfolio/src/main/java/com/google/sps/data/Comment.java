package com.google.sps.data;

/** A comment */
public final class Comment {

  private final long id;
  private final String commentText;
  private final long timestamp;

  public Comment(long id, String commentText, long timestamp) {
    this.id = id;
    this.commentText = commentText;
    this.timestamp = timestamp;
  }
}
