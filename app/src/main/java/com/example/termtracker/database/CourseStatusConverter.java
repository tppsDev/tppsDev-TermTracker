package com.example.termtracker.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.example.termtracker.entity.Course.CourseStatus;

public class CourseStatusConverter {
    @TypeConverter
    public static String toString(CourseStatus courseStatus) {
        return courseStatus == null ? null : courseStatus.toString();
    }

    @TypeConverter
    public static CourseStatus toCourseStatus(String courseStatusString) {
        return TextUtils.isEmpty(courseStatusString) ? null : CourseStatus.fromString(courseStatusString);
    }
}
