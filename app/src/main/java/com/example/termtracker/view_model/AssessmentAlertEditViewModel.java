package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.termtracker.database.AssessmentRepository;
import com.example.termtracker.database.InsertResult;
import com.example.termtracker.entity.AssessmentAlert;


public class AssessmentAlertEditViewModel extends AndroidViewModel {
    private AssessmentRepository assessmentRepository;


    public AssessmentAlertEditViewModel(@NonNull Application application) {
        super(application);
        assessmentRepository = AssessmentRepository.getRepository(application);
    }

    public void insertAssessmentAlert(AssessmentAlert  assessmentAlert, InsertResult resultInterface) {
        assessmentRepository.insertAssessmentAlert(assessmentAlert, resultInterface);
    }

    public void updateAssessmentAlert(AssessmentAlert assessmentAlert) {
        assessmentRepository.updateAssessmentAlert(assessmentAlert);
    }

}
