package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TermDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int termId;

    public TermDetailViewModelFactory(Application application, int termId) {
        this.application = application;
        this.termId = termId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TermDetailViewModel(application, termId);
    }
}
