package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles map marker data */
@WebServlet("/map-data")
public class MapServlet extends HttpServlet {

  private static final String MARKER_PARAM = "Marker";
  private static final String DESCRIP_PROP = "description";
  private static final String TIMESTAMP_PROP = "timestamp";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String description = getMarkerDescription(request);

    Entity markerEntity = new Entity(MARKER_PARAM);
    markerEntity.setProperty(DESCRIP_PROP, comment);
    markerEntity.setProperty(TIMESTAMP_PROP, timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(markerEntity);
    
    response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    return;
  }

  private String getMarkerDescription(HttpServletRequest request) {

  }

}