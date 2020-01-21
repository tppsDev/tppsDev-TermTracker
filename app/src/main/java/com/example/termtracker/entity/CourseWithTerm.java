package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class CourseWithTerm implements Parcelable {
    @Embedded
    private Course course;

    @Relation(parentColumn = "termId",
        entity = Term.class,
        entityColumn = "id")
    private Term term;

    @Ignore
    public CourseWithTerm() {
    }

    public CourseWithTerm(Course course, Term term) {
        this.course = course;
        this.term = term;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.course, flags);
        dest.writeParcelable(this.term, flags);
    }

    protected CourseWithTerm(Parcel in) {
        this.course = in.readParcelable(Course.class.getClassLoader());
        this.term = in.readParcelable(Term.class.getClassLoader());
    }

    public static final Parcelable.Creator<CourseWithTerm> CREATOR = new Parcelable.Creator<CourseWithTerm>() {
        @Override
        public CourseWithTerm createFromParcel(Parcel source) {
            return new CourseWithTerm(source);
        }

        @Override
        public CourseWithTerm[] newArray(int size) {
            return new CourseWithTerm[size];
        }
    };
}
