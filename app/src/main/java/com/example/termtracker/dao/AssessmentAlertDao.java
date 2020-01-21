package com.example.termtracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.AssessmentAlertWithAssessment;

import java.util.List;
@Dao
public interface AssessmentAlertDao {
    // CRUD
    @Insert
    Long insertAssessmentAlert(AssessmentAlert assessmentAlert);

    @Insert
    void insertAllAssessmentAlert(List<AssessmentAlert> assessmentAlerts);

    @Delete
    void deleteAssessmentAlert(AssessmentAlert assessmentAlert);

    @Query("SELECT * FROM assessmentAlert WHERE id = :id")
    AssessmentAlert getAssessmentAlertById(int id);

    @Query("SELECT * FROM assessmentAlert")
    LiveData<List<AssessmentAlert>> getAllAssessmentAlerts();

    @Transaction
    @Query("SELECT * FROM assessmentAlert WHERE id = :id")
    LiveData<AssessmentAlertWithAssessment> getAssessmentAlertWithAssessmentById(int id);

    @Transaction
    @Query("SELECT * FROM assessmentAlert")
    LiveData<List<AssessmentAlertWithAssessment>> getAllAssessmentAlertsWithAssessments();

    @Update
    void updateAssessmentAlert(AssessmentAlert assessmentAlert);

    @Query("DELETE FROM assessmentAlert")
    void deleteAllAssessmentAlerts();

    @Query("SELECT COUNT(*) FROM assessmentAlert")
    int getAllAssessmentAlertCount();
}
