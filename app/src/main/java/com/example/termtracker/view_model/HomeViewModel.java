package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.database.TermRepository;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.entity.TermWithCourses;
import com.example.termtracker.entity.TermWithCoursesWithAssessments;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private TermRepository termRepository;
    private CourseRepository courseRepository;
    private AssessmentRepository assessmentRepository;

    private LiveData<TermWithCoursesWithAssessments> currentTermsWithCoursesWithAssessments;
    private LiveData<List<CourseWithAssessments>> allCoursesWithAssessments;
    private LiveData<List<AssessmentWithCourse>> allAssessmentsWithCourseName;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        termRepository = TermRepository.getRepository(application);
        courseRepository = CourseRepository.getRepository(application);
        assessmentRepository = AssessmentRepository.getRepository(application);

        currentTermsWithCoursesWithAssessments = termRepository
                .getCurrentTermWithCoursesWithAssessments();
        allCoursesWithAssessments = courseRepository
                .getAllCoursesWithAssessments();
        allAssessmentsWithCourseName = assessmentRepository
                .getAllAssessmentsWithCourse();
    }

    public LiveData<TermWithCoursesWithAssessments> getCurrentTermWithCoursesWithAssessments() {
        return currentTermsWithCoursesWithAssessments;
    }

    public LiveData<List<CourseWithAssessments>> getAllCoursesWithAssessments() {
        return allCoursesWithAssessments;
    }

    public LiveData<List<AssessmentWithCourse>> getAllAssessmentsWithCourseName() {
        return allAssessmentsWithCourseName;
    }

}
