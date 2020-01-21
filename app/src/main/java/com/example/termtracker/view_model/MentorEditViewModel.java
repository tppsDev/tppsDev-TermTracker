package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.termtracker.database.MentorRepository;
import com.example.termtracker.entity.Mentor;

public class MentorEditViewModel extends AndroidViewModel {
    private MentorRepository mentorRepository;

    public MentorEditViewModel(@NonNull Application application) {
        super(application);
        mentorRepository = MentorRepository.getRepository(application);
    }

    public void insertMentor(Mentor mentor) {
        mentorRepository.insertMentor(mentor);
    }

    public void updateMentor(Mentor mentor) {
        mentorRepository.updateMentor(mentor);
    }
}
