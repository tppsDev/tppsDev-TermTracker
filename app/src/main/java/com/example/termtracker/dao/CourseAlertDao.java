package com.example.termtracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseAlertWithCourse;

import java.util.List;

@Dao
public interface CourseAlertDao {
    // crud
    @Insert
    Long insertCourseAlert(CourseAlert courseAlert);

    @Insert
    void insertAllCourseAlerts(List<CourseAlert> courseAlerts);

    @Update
    void updateCourseAlert(CourseAlert courseAlert);

    @Delete
    void deleteCourseAlert(CourseAlert courseAlert);

    @Query("SELECT * FROM courseAlert WHERE id = :id")
    CourseAlert getCourseAlertById(int id);

    @Query("SELECT * FROM  courseAlert")
    LiveData<List<CourseAlert>> getAllCourseAlerts();

    @Transaction
    @Query("SELECT * FROM courseAlert WHERE id = :id")
    LiveData<CourseAlertWithCourse> getCourseAlertWithCourseById(int id);

    @Transaction
    @Query("SELECT * FROM  courseAlert")
    LiveData<List<CourseAlertWithCourse>> getAllCourseAlertsWithCourses();

    @Query("DELETE FROM courseAlert")
    void deleteAllCourseAlerts();

    @Query("SELECT COUNT(*) FROM courseAlert")
    int getAllCourseAlertCount();
}
