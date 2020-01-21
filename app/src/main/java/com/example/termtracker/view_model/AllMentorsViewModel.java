package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.MentorRepository;
import com.example.termtracker.entity.Mentor;

import java.util.List;

public class AllMentorsViewModel extends AndroidViewModel {
    private MentorRepository mentorRepository;
    private LiveData<List<Mentor>> allMentors;

    public AllMentorsViewModel(@NonNull Application application) {
        super(application);
        mentorRepository = MentorRepository.getRepository(application);
        allMentors = mentorRepository.getAllMentors();
    }

    public LiveData<List<Mentor>> getAllMentors() {
        return allMentors;
    }
}
