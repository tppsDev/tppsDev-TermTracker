package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NoteDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int noteId;

    public NoteDetailViewModelFactory(Application application, int noteId) {
        this.application = application;
        this.noteId = noteId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NoteDetailViewModel(application, noteId);
    }
}
