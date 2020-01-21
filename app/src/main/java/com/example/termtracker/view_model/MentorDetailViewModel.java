package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.database.MentorRepository;
import com.example.termtracker.entity.Mentor;

public class MentorDetailViewModel extends AndroidViewModel {
    private MentorRepository mentorRepository;
    private LiveData<Mentor> mentor;

    public MentorDetailViewModel(@NonNull Application application, int id) {
        super(application);
        mentorRepository = MentorRepository.getRepository(application);
        mentor = mentorRepository.getMentorById(id);
    }

    public LiveData<Mentor> getMentor() {
        return mentor;
    }

    public void getCourseCountByMentorId(int mentorId, ConstraintCheck constraintCheck) {
        mentorRepository.getCourseCountByMentorId(mentorId, constraintCheck);
    }

    public void deleteMentor(Mentor mentor) {
        mentorRepository.deleteMentor(mentor);
    }
}
