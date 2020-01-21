package com.example.termtracker.dao;

import android.database.sqlite.SQLiteConstraintException;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.AssessmentWithAlerts;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.AssessmentWithNotes;

import java.util.List;

@Dao
public interface AssessmentDao {
    // CRUD methods
    @Insert
    void insertAssessment(Assessment assessment);

    @Insert
    void insertAllAssessments(List<Assessment> assessments);

    @Update
    void updateAssessment(Assessment assessment);

    @Delete
    void deleteAssessment(Assessment assessment);

    @Query("SELECT * FROM assessment WHERE id = :id")
    Assessment getAssessmentById(int id);

    @Query("SELECT * FROM assessment ORDER BY goalDate")
    LiveData<List<Assessment>> getAllAssessments();

    @Transaction
    @Query("SELECT * FROM assessment ORDER BY goalDate")
    LiveData<List<AssessmentWithCourse>> getAssessmentsWithCourse();

    @Transaction
    @Query("SELECT * FROM assessment WHERE id = :id")
    LiveData<AssessmentWithCourse> getAssessmentsWithCourseById(int id);

    @Transaction
    @Query("SELECT * FROM assessment")
    LiveData<List<AssessmentWithAlerts>> getAllAssessmentsWithAlerts();

    @Transaction
    @Query("SELECT * FROM assessment WHERE id = :id")
    LiveData<AssessmentWithAlerts> getAssessmentWithAlertsById(int id);

    @Transaction
    @Query("SELECT * FROM assessment")
    LiveData<List<AssessmentWithNotes>> getAllAssessmentsWithNotes();

    @Transaction
    @Query("SELECT * FROM assessment WHERE id = :id")
    LiveData<AssessmentWithNotes> getAssessmentWithNotesById(int id);

    @Query("DELETE FROM assessment")
    void deleteAllAssessments();

    @Query("SELECT COUNT(*) FROM assessmentAlert WHERE assessmentId = :assessmentId")
    int getAlertCountByAssessmentId(int assessmentId);

    @Query("SELECT COUNT(*) FROM assessment")
    int getAllAssessmentCount();
}
