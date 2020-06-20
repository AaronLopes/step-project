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

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // MEETING_REQUEST has name, duration in minutes, collection of attendees
    // event in EVENTS has name, time range, collection of attendees

    List<Event> overlapEvents = new ArrayList<Event>();
    List<TimeRange> overlapRanges = new ArrayList<TimeRange>();
    List<TimeRange> noOverlapRanges = new ArrayList<TimeRange>();
    Collection<String> requestAttendees = request.getAttendees();
    List<TimeRange> possibleRanges = new ArrayList<TimeRange>();
    int requestDuration = (int) request.getDuration();

    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return possibleRanges;
    }
   
    for (String attendee : requestAttendees) {
      for (Event event : events) {
        if (event.getAttendees().contains(attendee)) {
          overlapEvents.add(event);
        }
      }
    }

    if (overlapEvents.isEmpty()) {
      possibleRanges.add(TimeRange.WHOLE_DAY);
      return possibleRanges;
    }

    for (Event event : overlapEvents) {
      overlapRanges.add(event.getWhen());
    }

    Collections.sort(overlapRanges, TimeRange.ORDER_BY_START);
  
    int i = 0;
    while (i < overlapRanges.size()) {
      TimeRange currRange = overlapRanges.get(i);
      int j = i + 1;
      while (j < overlapRanges.size()) {
        TimeRange nextRange = overlapRanges.get(j);
        if (currRange.overlaps(nextRange)) {
          int modEnd = currRange.end() > nextRange.end() ? currRange.end() : nextRange.end();
          currRange = TimeRange.fromStartEnd(currRange.start(), modEnd, false);
          i = j;
          j++;
        } else {
          break;
        }
      }
      noOverlapRanges.add(currRange);
      i++;
    }

    if (noOverlapRanges.get(0).start() > TimeRange.START_OF_DAY) {
      possibleRanges.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, noOverlapRanges.get(0).start(), false));
    }

    i = 0;
    while (i < noOverlapRanges.size()) {
      TimeRange currRange = noOverlapRanges.get(i);
      int j = i + 1;
      if (j >= noOverlapRanges.size()) {
        if (currRange.end() < TimeRange.END_OF_DAY) {
          possibleRanges.add(TimeRange.fromStartEnd(currRange.end(), TimeRange.END_OF_DAY, true));
        }
      } else {
        possibleRanges.add(TimeRange.fromStartEnd(currRange.end(), noOverlapRanges.get(j).start(), false));
      }
      i++;
    }

    for (i = 0; i < possibleRanges.size(); i++) {
      if (possibleRanges.get(i).duration() < requestDuration) {
        possibleRanges.remove(i);
      }
    }

    return possibleRanges;
  }

}
