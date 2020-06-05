// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

/** Servlet that handles comment data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final String COMMENT_PARAM = "Comment";
  private static final String TEXT_PROP = "text";
  private static final String TIMESTAMP_PROP = "timestamp";
  private static final String COMMENT_BOX_PARAM = "comment-box";
  private static final String NUM_COMMENTS_PARAM = "comment-choice";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = getUserComment(request);
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity(COMMENT_PARAM);
    commentEntity.setProperty(TEXT_PROP, comment);
    commentEntity.setProperty(TIMESTAMP_PROP, timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    
    response.sendRedirect("/index.html");
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int numCommentDisplay = getNumDisplay(request);
    if (numCommentDisplay == -1) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter an integer between 1 and 10.");
      return;
    }

    Query query = new Query(COMMENT_PARAM).addSort(TIMESTAMP_PROP, SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    int i = 0;
    for (Entity entity : results.asIterable()) {
      if (i < numCommentDisplay) {
        long id = entity.getKey().getId();
        String commentText = (String) entity.getProperty(TEXT_PROP);
        long timestamp = (long) entity.getProperty(TIMESTAMP_PROP);

        Comment comment = new Comment(id, commentText, timestamp);
        comments.add(comment);  
        i++;
      }
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  private String getUserComment(HttpServletRequest request) {
    return request.getParameter(COMMENT_BOX_PARAM);
  }

  private int getNumDisplay(HttpServletRequest request) {
    String numCommentsString = request.getParameter(NUM_COMMENTS_PARAM);
    int numComments;
    try {
      numComments = Integer.parseInt(numCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numCommentsString);
      return -1;
    }

    if (numComments < 1 || numComments > 10) {
      System.err.println("Display choice is out of range: " + numCommentsString);
      return -1;
    }

    return numComments;
  }
}
