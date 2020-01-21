package com.example.termtracker.view_model;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.NoteRepository;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.AssessmentWithNotes;
import com.example.termtracker.entity.Note;

public class AssessmentDetailViewModel extends AndroidViewModel {
    private AssessmentRepository assessmentRepository;
    private NoteRepository noteRepository;
    private LiveData<AssessmentWithCourse> assessmentWithCourse;
    private LiveData<AssessmentWithNotes> assessmentWithNotes;

    public AssessmentDetailViewModel(@NonNull Application application, int assessmentId) {
        super(application);
        assessmentRepository = AssessmentRepository.getRepository(application);
        noteRepository = NoteRepository.getRepository(application);
        assessmentWithCourse = assessmentRepository.getAssessmentWithCourseById(assessmentId);
        assessmentWithNotes = assessmentRepository.getAssessmentWithNotesById(assessmentId);
    }

    public LiveData<AssessmentWithCourse> getAssessmentWithCourse() {
        return assessmentWithCourse;
    }

    public LiveData<AssessmentWithNotes> getAssessmentWithNotes() {
        return assessmentWithNotes;
    }

    public void deleteNote(Note note) {
        noteRepository.deleteNote(note);
    }
}
