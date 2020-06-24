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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> overlapRanges = new ArrayList<TimeRange>();
    List<TimeRange> noOverlapRanges = new ArrayList<TimeRange>();
    List<TimeRange> possibleRanges = new ArrayList<TimeRange>();
    int requestDuration = (int) request.getDuration();

    // 1 check edge case of invalid request
    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return new ArrayList<TimeRange>();
    }
   
   // 2 add events which overlap with required attendees to list
    for (String attendee : request.getAttendees()) {
      for (Event event : events) {
        if (event.getAttendees().contains(attendee) && !overlapRanges.contains(attendee)) {
          overlapRanges.add(event.getWhen());
        }
      }
    }

    // 3 if no overlapping attendees, return whole day
    if (overlapRanges.isEmpty()) {
      return new ArrayList<TimeRange>(Arrays.asList(TimeRange.WHOLE_DAY));
    }

    // 4 sort the overlapping ranges
    Collections.sort(overlapRanges, TimeRange.ORDER_BY_START);
  
    // 5 check if next range overlaps current range, if so merge the range
    for (TimeRange range : overlapRanges) {
      if (noOverlapRanges.isEmpty()) {
        noOverlapRanges.add(range);
      } else {
        int prevIndex = noOverlapRanges.size() - 1;
        TimeRange prevRange = noOverlapRanges.get(prevIndex);
        if (range.overlaps(prevRange) && prevRange.end() < range.end()) {
          noOverlapRanges.set(prevIndex, TimeRange.fromStartEnd(prevRange.start(), range.end(), false));
        } else if (!range.overlaps(prevRange)) {
          noOverlapRanges.add(range);
        }
      }
    }
   
    // 6 check if their is a gap between start of day and first range
    if (noOverlapRanges.get(0).start() > TimeRange.START_OF_DAY) {
      possibleRanges.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, noOverlapRanges.get(0).start(), false));
    }

    // 7 iterate over ranges and add gaps to possible range list
    int i = 0;
    while (i < noOverlapRanges.size()) {
      TimeRange currRange = noOverlapRanges.get(i);
      int j = i + 1;
      if (j >= noOverlapRanges.size()) {
        possibleRanges.add(TimeRange.fromStartEnd(currRange.end(), TimeRange.END_OF_DAY, true));
      } else {
        possibleRanges.add(TimeRange.fromStartEnd(currRange.end(), noOverlapRanges.get(j).start(), false));
      }
      i++;
    }


    // 8 return ranges which are geq request duration
    return filterRanges(possibleRanges, requestDuration);
  }

  public Collection<TimeRange> filterRanges(Collection<TimeRange> ranges, int duration) {
    List<TimeRange> filteredRanges = new ArrayList<TimeRange>();
    for (TimeRange range : ranges) {
      if (range.duration() >= duration) {
        filteredRanges.add(range);
      }
    }
    return filteredRanges;
  }

}
