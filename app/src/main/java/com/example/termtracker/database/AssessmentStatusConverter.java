package com.example.termtracker.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.example.termtracker.entity.Assessment.*;

public class AssessmentStatusConverter {
    @TypeConverter
    public static String toString(AssessmentStatus assessmentStatus) {
        return assessmentStatus == null ? null : assessmentStatus.toString();
    }

    @TypeConverter
    public static AssessmentStatus toAssessmentStatus(String assessmentStatusString) {
        return TextUtils.isEmpty(assessmentStatusString) ? null : AssessmentStatus.fromString(assessmentStatusString);
    }
}
