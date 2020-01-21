package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

public class CourseWithAlerts {
    @Embedded
    private Course course;
    @Relation(parentColumn = "id",
            entity = CourseAlert.class,
            entityColumn = "courseId")
    private List<CourseAlert> courseAlerts;

    @Ignore
    public CourseWithAlerts() {
    }

    public CourseWithAlerts(Course course, List<CourseAlert> courseAlerts) {
        this.course = course;
        this.courseAlerts = courseAlerts;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<CourseAlert> getCourseAlerts() {
        return courseAlerts;
    }

    public void setCourseAlerts(List<CourseAlert> courseAlerts) {
        this.courseAlerts = courseAlerts;
    }
}
