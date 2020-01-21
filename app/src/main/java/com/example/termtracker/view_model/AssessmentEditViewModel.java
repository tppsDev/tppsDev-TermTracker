package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.Course;

import java.util.List;

public class AssessmentEditViewModel extends AndroidViewModel {
    private AssessmentRepository assessmentRepository;
    private CourseRepository courseRepository;
    private LiveData<List<Course>> allCourses;

    public AssessmentEditViewModel(@NonNull Application application) {
        super(application);
        assessmentRepository = AssessmentRepository.getRepository(application);
        courseRepository = CourseRepository.getRepository(application);
        allCourses = courseRepository.getAllCourses();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public void insertAssessment(Assessment assessment) {
        assessmentRepository.insertAssessment(assessment);
    }

    public void updateAssessment(Assessment assessment) {
        assessmentRepository.updateAssessment(assessment);
    }
}
