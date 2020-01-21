package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TermWithCoursesWithAssessments {
    @Embedded
    private Term term;
    @Relation(parentColumn = "id",
            entity = Course.class,
            entityColumn = "termId")
    private List<CourseWithAssessments> courses;

    public TermWithCoursesWithAssessments(Term term, List<CourseWithAssessments> courses) {
        this.term = term;
        this.courses = courses;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<CourseWithAssessments> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseWithAssessments> courses) {
        this.courses = courses;
    }
}
