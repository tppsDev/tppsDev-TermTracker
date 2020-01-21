package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CourseWithAssessments {
    @Embedded
    private Course course;
    @Relation(
            parentColumn = "id",
            entityColumn = "courseId",
            entity = Assessment.class)
    private List<Assessment> assessments;

    public CourseWithAssessments(Course course, List<Assessment> assessments) {
        this.course = course;
        this.assessments = assessments;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
    }
}
