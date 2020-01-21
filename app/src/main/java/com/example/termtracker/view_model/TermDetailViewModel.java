package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.database.TermRepository;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.TermWithCourses;

public class TermDetailViewModel extends AndroidViewModel {
    private TermRepository termRepository;
    private CourseRepository courseRepository;
    private LiveData<TermWithCourses> term;

    public TermDetailViewModel(@NonNull Application application, int termId) {
        super(application);
        termRepository = TermRepository.getRepository(application);
        courseRepository = CourseRepository.getRepository(application);
        term = termRepository.getTermWithCourses(termId);
    }

    public LiveData<TermWithCourses> getTermWithCourses() {
        return term;
    }

    public void deleteCourse(Course course) {
        courseRepository.deleteCourse(course);
    }
}
