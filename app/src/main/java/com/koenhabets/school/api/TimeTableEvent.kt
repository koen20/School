package com.koenhabets.school.api

import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import java.util.Calendar

class TimeTableEvent(val id: Long, val title: String, val location: String, val startTime: Calendar, val endTime: Calendar, val color: Int): WeekViewDisplayable<TimeTableEvent> {

    override fun toWeekViewEvent(): WeekViewEvent<TimeTableEvent> {
        return WeekViewEvent(id, title, startTime, endTime, location, color, false, this)
    }
}
//private boolean cancelled;
//private boolean modified;
//private String changeDescription;
