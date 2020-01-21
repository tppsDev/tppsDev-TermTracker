package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.database.NoteRepository;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.entity.CourseWithMentors;
import com.example.termtracker.entity.CourseWithNotes;
import com.example.termtracker.entity.CourseWithTerm;
import com.example.termtracker.entity.Note;

public class CourseDetailViewModel extends AndroidViewModel {
    private CourseRepository courseRepository;
    private AssessmentRepository assessmentRepository;
    private NoteRepository noteRepository;
    private LiveData<CourseWithTerm> course;
    private LiveData<CourseWithAssessments> courseWithAssessments;
    private LiveData<CourseWithNotes> courseWithNotes;
    private LiveData<CourseWithMentors> courseWithMentors;

    public CourseDetailViewModel(@NonNull Application application, int courseId) {
        super(application);
        courseRepository = CourseRepository.getRepository(application);
        assessmentRepository = AssessmentRepository.getRepository(application);
        noteRepository = NoteRepository.getRepository(application);
        course = courseRepository.getCourseWithTermById(courseId);
        courseWithAssessments = courseRepository.getCourseWithAssessments(courseId);
        courseWithNotes = courseRepository.getCourseWithNotes(courseId);
        courseWithMentors = courseRepository.getCourseWithMentorsByCourseId(courseId);
    }

    public LiveData<CourseWithTerm> getCourse() {
        return course;
    }

    public LiveData<CourseWithAssessments> getCourseWithAssessments() {
        return courseWithAssessments;
    }

    public LiveData<CourseWithNotes> getCourseWithNotes() {
        return courseWithNotes;
    }

    public LiveData<CourseWithMentors> getCourseWithMentors() {
        return courseWithMentors;
    }

    public void getAlertCountByAssessmentId(int assessmentId, ConstraintCheck constraintCheck) {
        assessmentRepository.getAlertCountByAssessmentId(assessmentId, constraintCheck);
    }

    public void deleteAssessment(Assessment assessment) {
        assessmentRepository.deleteAssessment(assessment);
    }

    public void deleteNote(Note note) {
        noteRepository.deleteNote(note);
    }
}
