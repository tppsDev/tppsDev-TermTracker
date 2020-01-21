package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.termtracker.view_model.AssessmentDetailViewModel;

public class AssessmentDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int assessmentId;

    public AssessmentDetailViewModelFactory(Application application, int assessmentId) {
        this.application = application;
        this.assessmentId = assessmentId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AssessmentDetailViewModel(application, assessmentId);
    }
}
