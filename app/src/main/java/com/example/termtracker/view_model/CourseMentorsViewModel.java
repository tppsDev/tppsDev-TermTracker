package com.example.termtracker.view_model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.CourseWithMentors;

public class CourseMentorsViewModel extends AndroidViewModel {
    private CourseRepository courseRepository;
    private LiveData<CourseWithMentors> courseWithMentors;

    public CourseMentorsViewModel(@NonNull Application application) {
        super(application);
        courseRepository = CourseRepository.getRepository(application);
    }

    public LiveData<CourseWithMentors> getCourseWithMentorsByCourseId(int courseId) {
        return courseRepository.getCourseWithMentorsByCourseId(courseId);
    }

    public void insertCourseMentorXRef(CourseMentorXRef courseMentorXRef) {
        courseRepository.insertCourseMentorXRef(courseMentorXRef);
    }

    public void deleteCourseMentorXRef(CourseMentorXRef courseMentorXRef) {
        courseRepository.deleteCourseMentorXRef(courseMentorXRef);
    }
}
