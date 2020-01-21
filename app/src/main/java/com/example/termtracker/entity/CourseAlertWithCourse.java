package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class CourseAlertWithCourse implements Parcelable {
    @Embedded
    CourseAlert courseAlert;

    @Relation(parentColumn = "courseId",
        entity = Course.class,
        entityColumn = "id")
    Course course;

    @Ignore
    public CourseAlertWithCourse() {
    }

    public CourseAlertWithCourse(CourseAlert courseAlert, Course course) {
        this.courseAlert = courseAlert;
        this.course = course;
    }

    public CourseAlert getCourseAlert() {
        return courseAlert;
    }

    public void setCourseAlert(CourseAlert courseAlert) {
        this.courseAlert = courseAlert;
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
        dest.writeParcelable(this.courseAlert, flags);
        dest.writeParcelable(this.course, flags);
    }

    protected CourseAlertWithCourse(Parcel in) {
        this.courseAlert = in.readParcelable(CourseAlert.class.getClassLoader());
        this.course = in.readParcelable(Course.class.getClassLoader());
    }

    public static final Parcelable.Creator<CourseAlertWithCourse> CREATOR = new Parcelable.Creator<CourseAlertWithCourse>() {
        @Override
        public CourseAlertWithCourse createFromParcel(Parcel source) {
            return new CourseAlertWithCourse(source);
        }

        @Override
        public CourseAlertWithCourse[] newArray(int size) {
            return new CourseAlertWithCourse[size];
        }
    };
}
