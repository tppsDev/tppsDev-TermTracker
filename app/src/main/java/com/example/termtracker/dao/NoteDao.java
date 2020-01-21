package com.example.termtracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.termtracker.entity.Note;
import com.example.termtracker.entity.NoteWithCourse;

import java.util.List;

@Dao
public interface NoteDao {
    // CRUD methods
    @Insert
    void insertNote(Note note);

    @Insert
    void insertAllNotes(List<Note> notes);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM note WHERE id = :id")
    Note getNoteById(int id);

    @Query("SELECT * FROM note")
    LiveData<List<Note>> getAllNotes();

    @Transaction
    @Query("SELECT * FROM note")
    LiveData<List<NoteWithCourse>> getAllNotesWithCourses();

    @Transaction
    @Query("SELECT * FROM note WHERE id = :id")
    LiveData<NoteWithCourse> getNotesWithCourseById(int id);

    @Query("DELETE FROM note")
    void deleteAllNotes();

    @Query("SELECT COUNT(*) FROM note")
    int getAllNoteCount();
}
