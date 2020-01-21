package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class AssessmentWithCourse implements Parcelable {
    @Embedded
    private Assessment assessment;

    @Relation(parentColumn = "courseId",
        entity = Course.class,
            entityColumn = "id"
    )
    private Course course;

    @Ignore
    public AssessmentWithCourse() {
    }

    public AssessmentWithCourse(Assessment assessment, Course course) {
        this.assessment = assessment;
        this.course = course;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.assessment, flags);
        dest.writeParcelable(this.course, flags);
    }

    protected AssessmentWithCourse(Parcel in) {
        this.assessment = in.readParcelable(Assessment.class.getClassLoader());
        this.course = in.readParcelable(Course.class.getClassLoader());
    }

    public static final Parcelable.Creator<AssessmentWithCourse> CREATOR = new Parcelable.Creator<AssessmentWithCourse>() {
        @Override
        public AssessmentWithCourse createFromParcel(Parcel source) {
            return new AssessmentWithCourse(source);
        }

        @Override
        public AssessmentWithCourse[] newArray(int size) {
            return new AssessmentWithCourse[size];
        }
    };
}
