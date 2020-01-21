package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.termtracker.database.NoteRepository;
import com.example.termtracker.entity.Note;

public class NoteEditViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;

    public NoteEditViewModel(@NonNull Application application) {
        super(application);
        noteRepository = NoteRepository.getRepository(application);
    }

    public void insertNote(Note note) {
        noteRepository.insertNote(note);
    }

    public void updateNote(Note note) {
        noteRepository.updateNote(note);
    }
}
