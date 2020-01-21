package com.example.termtracker.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.termtracker.dao.NoteDao;
import com.example.termtracker.entity.Note;
import com.example.termtracker.entity.NoteWithCourse;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;

    private LiveData<List<Note>> allNotes;
    private LiveData<List<NoteWithCourse>> allNotesWithCourses;

    private static volatile NoteRepository INSTANCE;

    public static NoteRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (NoteRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoteRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private NoteRepository(Application application) {
        TermTrackerDatabase db = TermTrackerDatabase.getDatebase(application);
        noteDao = db.noteDao();
        allNotes = noteDao.getAllNotes();
        allNotesWithCourses = noteDao.getAllNotesWithCourses();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<NoteWithCourse>> getAllNotesWithCourses() {
        return allNotesWithCourses;
    }

    public LiveData<NoteWithCourse> getNoteWithCourseById(int id) {
        return noteDao.getNotesWithCourseById(id);
    }

    public void insertNote(Note note) {
        new insertNoteAsyncTask(noteDao).execute(note);
    }

    private static class insertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncTaskNoteDao;
        public insertNoteAsyncTask(NoteDao noteDao) {
            asyncTaskNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            asyncTaskNoteDao.insertNote(notes[0]);
            return null;
        }
    }

    public void updateNote(Note note) {
        new updateNoteAsyncTask(noteDao).execute(note);
    }

    private static class updateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncTaskNoteDao;
        public updateNoteAsyncTask(NoteDao noteDao) {
            asyncTaskNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            asyncTaskNoteDao.updateNote(notes[0]);
            return null;
        }
    }

    public void deleteNote(Note note) {
        new deleteNoteAsyncTask(noteDao).execute(note);
    }

    private static class deleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao asyncTaskNoteDao;
        public deleteNoteAsyncTask(NoteDao noteDao) {
            asyncTaskNoteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            asyncTaskNoteDao.deleteNote(notes[0]);
            return null;
        }
    }
}
