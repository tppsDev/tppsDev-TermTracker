package com.example.termtracker.database;



import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.example.termtracker.entity.Assessment.*;

public class AssessmentTypeConverter {
    @TypeConverter
    public static String toString(AssessmentType assessmentType) {
        return assessmentType == null ? null : assessmentType.toString();
    }

    @TypeConverter
    public static AssessmentType toAssessmentType(String assessmentTypeString) {
        return TextUtils.isEmpty(assessmentTypeString) ? null : AssessmentType.fromString(assessmentTypeString);
    }
}
