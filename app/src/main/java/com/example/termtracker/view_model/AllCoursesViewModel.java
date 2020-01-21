package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseWithTerm;

import java.util.List;

public class AllCoursesViewModel extends AndroidViewModel {
    private CourseRepository courseRepository;
    private LiveData<List<CourseWithTerm>>  allCoursesWithTerms;

    public AllCoursesViewModel(@NonNull Application application) {
        super(application);
        courseRepository = CourseRepository.getRepository(application);
        allCoursesWithTerms = courseRepository.getAllCoursesWithTerms();
    }

    public LiveData<List<CourseWithTerm>> getAllCoursesWithTerms() {
        return allCoursesWithTerms;
    }

    public void deleteCourse(Course course) {
        courseRepository.deleteCourse(course);
    }
}
