package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

public class CourseWithNotes {
    @Embedded
    private Course course;
    @Relation(parentColumn = "id",
            entity = Note.class,
            entityColumn = "courseId")
    private List<Note> notes;

    @Ignore
    public CourseWithNotes() {
    }

    public CourseWithNotes(Course course, List<Note> notes) {
        this.course = course;
        this.notes = notes;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
