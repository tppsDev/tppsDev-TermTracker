package com.example.termtracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

public class NoteWithCourse implements Parcelable {
    @Embedded
    private Note note;

    @Relation(parentColumn = "courseId",
        entity = Course.class,
        entityColumn = "id")
    private Course course;

    @Ignore
    public NoteWithCourse() {
    }

    public NoteWithCourse(Note note, Course course) {
        this.note = note;
        this.course = course;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
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
        dest.writeParcelable(this.note, flags);
        dest.writeParcelable(this.course, flags);
    }

    protected NoteWithCourse(Parcel in) {
        this.note = in.readParcelable(Note.class.getClassLoader());
        this.course = in.readParcelable(Course.class.getClassLoader());
    }

    public static final Parcelable.Creator<NoteWithCourse> CREATOR = new Parcelable.Creator<NoteWithCourse>() {
        @Override
        public NoteWithCourse createFromParcel(Parcel source) {
            return new NoteWithCourse(source);
        }

        @Override
        public NoteWithCourse[] newArray(int size) {
            return new NoteWithCourse[size];
        }
    };
}
