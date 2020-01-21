package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class CourseWithMentors implements Parcelable {
    @Embedded
    private Course course;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            entity = Mentor.class,
            associateBy = @Junction(
                    value = CourseMentorXRef.class,
                    parentColumn = "courseId",
                    entityColumn = "mentorId"
            ))
    private List<Mentor> courseMentors;

    @Ignore
    public CourseWithMentors() {
    }

    public CourseWithMentors(Course course, List<Mentor> courseMentors) {
        this.course = course;
        this.courseMentors = courseMentors;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Mentor> getCourseMentors() {
        return courseMentors;
    }

    public void setCourseMentors(List<Mentor> courseMentors) {
        this.courseMentors = courseMentors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.course, flags);
        dest.writeTypedList(this.courseMentors);
    }

    protected CourseWithMentors(Parcel in) {
        this.course = in.readParcelable(Course.class.getClassLoader());
        this.courseMentors = in.createTypedArrayList(Mentor.CREATOR);
    }

    public static final Parcelable.Creator<CourseWithMentors> CREATOR = new Parcelable.Creator<CourseWithMentors>() {
        @Override
        public CourseWithMentors createFromParcel(Parcel source) {
            return new CourseWithMentors(source);
        }

        @Override
        public CourseWithMentors[] newArray(int size) {
            return new CourseWithMentors[size];
        }
    };
}
