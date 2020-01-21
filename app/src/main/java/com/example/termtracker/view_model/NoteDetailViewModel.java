package com.example.termtracker.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.termtracker.database.NoteRepository;
import com.example.termtracker.entity.Note;
import com.example.termtracker.entity.NoteWithCourse;

public class NoteDetailViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private LiveData<NoteWithCourse> note;

    public NoteDetailViewModel(@NonNull Application application, int noteId) {
        super(application);
        noteRepository = NoteRepository.getRepository(application);
        note = noteRepository.getNoteWithCourseById(noteId);
    }

    public LiveData<NoteWithCourse> getNote() {
        return note;
    }

    public void deleteNote(Note note) {
        noteRepository.deleteNote(note);
    }
}
