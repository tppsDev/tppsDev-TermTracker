package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

public class TermWithCourses {
    @Embedded
    private Term term;
    @Relation(parentColumn = "id",
            entity = Course.class,
            entityColumn = "termId")
    private List<Course> courses;

    @Ignore
    public TermWithCourses() {
    }

    public TermWithCourses(Term term, List<Course> courses) {
        this.term = term;
        this.courses = courses;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
