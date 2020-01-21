package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.AssessmentAlertWithAssessment;
import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.CourseAlertWithCourse;

import java.util.List;

public class AllAlertsViewModel extends AndroidViewModel {
    private AssessmentRepository assessmentRepository;
    private CourseRepository courseRepository;
    private LiveData<List<AssessmentAlertWithAssessment>> allAssessmentAlertsWithAssessments;
    private LiveData<List<CourseAlertWithCourse>> allCourseAlertsWithCourses;

    public AllAlertsViewModel(@NonNull Application application) {
        super(application);
        assessmentRepository = AssessmentRepository.getRepository(application);
        courseRepository = CourseRepository.getRepository(application);

        allAssessmentAlertsWithAssessments = assessmentRepository.getAllAssessmentAlertsWithAssessments();
        allCourseAlertsWithCourses = courseRepository.getAllCourseAlertsWithCourses();
    }

    public LiveData<List<AssessmentAlertWithAssessment>> getAllAssessmentAlertsWithAssessments() {
        return allAssessmentAlertsWithAssessments;
    }

    public LiveData<List<CourseAlertWithCourse>> getAllCourseAlertsWithCourses() {
        return allCourseAlertsWithCourses;
    }

    public void deleteAssessmentAlert(AssessmentAlert alert) {
        assessmentRepository.deleteAssessmentAlert(alert);
    }

    public void deleteCourseAlert(CourseAlert alert) {
        courseRepository.deleteCourseAlert(alert);
    }
}
