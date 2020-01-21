package com.example.termtracker.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

public class AssessmentWithNotes {
    @Embedded
    private Assessment assessment;

    @Relation(parentColumn = "courseId",
            entity = Note.class,
            entityColumn = "courseId")
    private List<Note> notes;

    @Ignore
    public AssessmentWithNotes() {
    }

    public AssessmentWithNotes(Assessment assessment, List<Note> notes) {
        this.assessment = assessment;
        this.notes = notes;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
