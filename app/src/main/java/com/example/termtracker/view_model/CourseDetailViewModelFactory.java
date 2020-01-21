package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CourseDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int courseId;

    public CourseDetailViewModelFactory(Application application, int courseId) {
        this.application = application;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CourseDetailViewModel(application, courseId);
    }
}
