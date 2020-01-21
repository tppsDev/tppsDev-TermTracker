package com.example.termtracker.view_model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.database.TermRepository;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.Term;

import java.util.List;

public class CourseEditViewModel extends AndroidViewModel {
    private CourseRepository courseRepository;
    private TermRepository termRepository;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Term>> allTerms;

    public CourseEditViewModel(@NonNull Application application) {
        super(application);

        courseRepository = CourseRepository.getRepository(application);
        termRepository = TermRepository.getRepository(application);

        allCourses = courseRepository.getAllCourses();
        allTerms = termRepository.getAllTerms();
    }

    public LiveData<List<Course>> getAllCourses() { return allCourses; }

    public LiveData<List<Term>> getAllTerms() {
        return allTerms;
    }

    public void insertCourse(Course course) { courseRepository.insertCourse(course);}

    public void  updateCourse(Course course) { courseRepository.updateCourse(course);}
}
