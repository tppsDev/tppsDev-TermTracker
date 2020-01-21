package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MentorDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int id;

    public MentorDetailViewModelFactory(Application application, int id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MentorDetailViewModel(application, id);
    }
}
