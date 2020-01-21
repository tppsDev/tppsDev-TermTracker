package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.TermRepository;
import com.example.termtracker.entity.Term;
import com.example.termtracker.entity.TermWithCourses;

import java.util.List;

public class AllTermsViewModel extends AndroidViewModel {
    private TermRepository termRepository;
    private LiveData<List<TermWithCourses>> allTermsWithCourses;

    public AllTermsViewModel(@NonNull Application application) {
        super(application);
        termRepository = TermRepository.getRepository(application);

        allTermsWithCourses = termRepository.getAllTermsWithCourses();
    }

    public LiveData<List<TermWithCourses>> getAllTermsWithCourses() {
        return allTermsWithCourses;
    }

    public int getTermCourseCount(int termID) {
        return termRepository.getTermsCourseCount(termID);
    }

    public void deleteTerm(Term term) {
        termRepository.deleteTerm(term);
    }
}
