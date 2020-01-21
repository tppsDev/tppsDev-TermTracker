package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.database.InsertResult;
import com.example.termtracker.entity.CourseAlert;

public class CourseAlertEditViewModel extends AndroidViewModel {
    private CourseRepository courseRepository;


    public CourseAlertEditViewModel(@NonNull Application application) {
        super(application);
        courseRepository = CourseRepository.getRepository(application);
    }

    public void insertCourseAlert(CourseAlert courseAlert, InsertResult resultInterface) {
        courseRepository.insertCourseAlert(courseAlert, resultInterface);
    }

    public void updateCourseAlert(CourseAlert courseAlert) {
        courseRepository.updateCourseAlert(courseAlert);
    }
}
