package com.example.termtracker.view_model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.TermRepository;
import com.example.termtracker.entity.Term;

import java.util.List;

public class TermEditViewModel extends AndroidViewModel {
    private TermRepository termRepository;
    private LiveData<List<Term>> allTerms;

    public TermEditViewModel(@NonNull Application application) {
        super(application);

        termRepository = TermRepository.getRepository(application);

        allTerms = termRepository.getAllTerms();
    }

    public LiveData<List<Term>> getAllTerms() {
        return allTerms;
    }

    public void insertTerm(Term term) {
        termRepository.insertTerm(term);
    }

    public void updateTerm(Term term) { termRepository.updateTerm(term); }
}
