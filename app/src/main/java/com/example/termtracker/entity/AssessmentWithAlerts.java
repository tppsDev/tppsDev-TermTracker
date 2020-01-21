package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

public class AssessmentWithAlerts {
    @Embedded
    private Assessment assessment;
    @Relation(parentColumn = "id",
            entity = AssessmentAlert.class,
            entityColumn = "assessmentId")
    private List<AssessmentAlert> assessmentAlerts;

    @Ignore
    public AssessmentWithAlerts() {
    }

    public AssessmentWithAlerts(Assessment assessment, List<AssessmentAlert> assessmentAlerts) {
        this.assessment = assessment;
        this.assessmentAlerts = assessmentAlerts;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public List<AssessmentAlert> getAssessmentAlerts() {
        return assessmentAlerts;
    }

    public void setAssessmentAlerts(List<AssessmentAlert> assessmentAlerts) {
        this.assessmentAlerts = assessmentAlerts;
    }
}
