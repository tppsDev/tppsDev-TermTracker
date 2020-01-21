package com.example.termtracker.view_model;


import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.AssessmentWithCourse;

import java.util.List;

public class AllAssessmentsViewModel extends AndroidViewModel {
    private AssessmentRepository assessmentRepository;
    private LiveData<List<AssessmentWithCourse>> allAssessmentsWithCourses;

    public AllAssessmentsViewModel(@NonNull Application application) {
        super(application);
        assessmentRepository = AssessmentRepository.getRepository(application);
        allAssessmentsWithCourses = assessmentRepository.getAllAssessmentsWithCourse();
    }

    public LiveData<List<AssessmentWithCourse>> getAllAssessmentsWithCourses() {
        return allAssessmentsWithCourses;
    }

    public void deleteAssessment(Assessment assessment) {
        assessmentRepository.deleteAssessment(assessment);
    }

    public void getAlertCountByAssessmentId(int assessmentId, ConstraintCheck constraintCheck) {
        assessmentRepository.getAlertCountByAssessmentId(assessmentId, constraintCheck);
    }
}
