package com.example.termtracker.dao;

import android.view.MenuItem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.termtracker.entity.Mentor;

import java.util.List;

@Dao
public interface MentorDao {
    // CRUD methods
    @Insert
    void insertMentor(Mentor mentor);

    @Insert
    void insertAllMentors(List<Mentor> mentors);

    @Update
    void updateMentor(Mentor mentor);

    @Delete
    void deleteMentor(Mentor mentor);

    @Query("SELECT * FROM mentor WHERE id = :id")
    LiveData<Mentor> getMentorById(int id);

    @Query("SELECT * FROM mentor ORDER BY lastName")
    LiveData<List<Mentor>> getAllMentors();

    @Query("SELECT * FROM courseMentor WHERE mentorId = :mentorId")
    int getCourseCountByMentorId(int mentorId);

    @Query("DELETE FROM mentor")
    void deleteAllMentors();

    @Query("SELECT COUNT(*) FROM mentor")
    int getAllMentorCount();
}
