package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.database.CourseRepository;
import com.example.termtracker.database.MentorRepository;
import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.Mentor;

import java.util.List;

public class MentorSelectViewModel extends AndroidViewModel {
    private MentorRepository mentorRepository;
    private CourseRepository courseRepository;

    private LiveData<List<Mentor>> allMentors;

    public MentorSelectViewModel(@NonNull Application application) {
        super(application);
        mentorRepository = MentorRepository.getRepository(application);
        courseRepository = CourseRepository.getRepository(application);
        allMentors = mentorRepository.getAllMentors();
    }

    public LiveData<List<Mentor>> getAllMentors() {
        return allMentors;
    }

    public void getCourseCountByMentorId(int mentorId, ConstraintCheck constraintCheck) {
        mentorRepository.getCourseCountByMentorId(mentorId, constraintCheck);
    }

    public void insertCourseMentorXref(CourseMentorXRef courseMentorXRef) {
        courseRepository.insertCourseMentorXRef(courseMentorXRef);
    }

    public void deleteMentor(Mentor mentor) {
        mentorRepository.deleteMentor(mentor);
    }
}
