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
    ArrayList<TimeRange> overlapRanges = new ArrayList<TimeRange>();
    int requestDuration = (int) request.getDuration();

    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return new ArrayList<TimeRange>();
    }

    for (Event event : events) {
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          overlapRanges.add(event.getWhen());
          break;
        }
      }
    }
   
    if (overlapRanges.isEmpty()) {
      return new ArrayList<TimeRange>(Arrays.asList(TimeRange.WHOLE_DAY));
    }

    Collections.sort(overlapRanges, TimeRange.ORDER_BY_START);
  
    ArrayList<TimeRange> noOverlapRanges = mergeRanges(overlapRanges);
    ArrayList<TimeRange> possibleRanges = retrievePossibleRanges(noOverlapRanges);
    
    return filterRanges(possibleRanges, requestDuration);
  }

  /**
   * Return a list of {@code TimeRange}'s which represent the gap(s) of time
   * between event ranges.
   * 
   */
  public ArrayList<TimeRange> retrievePossibleRanges(ArrayList<TimeRange> ranges) {
    ArrayList<TimeRange> possibleRanges = new ArrayList<TimeRange>();
    if (ranges.get(0).start() > TimeRange.START_OF_DAY) {
      possibleRanges.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, ranges.get(0).start(), false));
    }
    for (int i = 0; i < ranges.size(); i++) {
      TimeRange currRange = ranges.get(i);
      if (i + 1 >= ranges.size()) {
        possibleRanges.add(TimeRange.fromStartEnd(currRange.end(), TimeRange.END_OF_DAY, true));
      } else {
        possibleRanges.add(TimeRange.fromStartEnd(currRange.end(), ranges.get(i + 1).start(), false));
      }
    }
    return possibleRanges;
  }

  /**
   * Return a list of {@code TimeRange}'s sorted by start time, where overlapping
   * ranges are merged into a single range.
   * 
   */
  public ArrayList<TimeRange> mergeRanges(ArrayList<TimeRange> ranges) {
    ArrayList<TimeRange> mergedRanges = new ArrayList<TimeRange>();
    for (TimeRange range : ranges) {
      if (mergedRanges.isEmpty()) {
        mergedRanges.add(range);
      } else {
        int prevIndex = mergedRanges.size() - 1;
        TimeRange prevRange = mergedRanges.get(prevIndex);
        if (range.overlaps(prevRange) && prevRange.end() < range.end()) {
          mergedRanges.set(prevIndex, TimeRange.fromStartEnd(prevRange.start(), range.end(), false));
        } else if (!range.overlaps(prevRange)) {
          mergedRanges.add(range);
        }
      }
    }
    return mergedRanges;
  }


  /**
   * Return a list of {@code TimeRange}'s which fit the {@code MeetingRequest} 
   * duration criteria.
   */
  public ArrayList<TimeRange> filterRanges(ArrayList<TimeRange> ranges, int duration) {
    ArrayList<TimeRange> filteredRanges = new ArrayList<TimeRange>();
    for (TimeRange range : ranges) {
      if (range.duration() >= duration) {
        filteredRanges.add(range);
      }
    }
    return filteredRanges;
  }

}
