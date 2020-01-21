package com.example.termtracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseWithAlerts;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.entity.CourseWithMentors;
import com.example.termtracker.entity.CourseWithNotes;
import com.example.termtracker.entity.CourseWithTerm;

import java.util.List;

@Dao
public interface CourseDao {
    // CRUD methods
    @Insert
    void insertCourse(Course course);

    @Insert
    void insertAllCourses(List<Course> courses);

    @Update
    void updateCourse(Course course);

    @Delete
    void deleteCourse(Course course);

    @Query("SELECT * FROM course WHERE id = :id")
    Course getCourseById(int id);

    @Query("SELECT * FROM course ORDER BY startDate")
    LiveData<List<Course>> getAllCourses();

    @Transaction
    @Query("SELECT * FROM course WHERE id = :id")
    LiveData<CourseWithTerm> getCoursewithTermById(int id);

    @Transaction
    @Query("SELECT * FROM course ORDER BY startDate")
    LiveData<List<CourseWithTerm>> getAllCoursesWithTerms();

    @Transaction
    @Query("SELECT * FROM course WHERE id = :id")
    LiveData<CourseWithAssessments> getCourseWithAssessmentsById(int id);

    @Transaction
    @Query("SELECT * FROM course")
    LiveData<List<CourseWithAssessments>> getAllCoursesWithAssessments();

    @Transaction
    @Query("SELECT * FROM course WHERE id = :id")
    LiveData<CourseWithAlerts> getCourseWithAlertsById(int id);

    @Transaction
    @Query("SELECT * FROM course")
    LiveData<List<CourseWithAlerts>> getAllCoursesWithAlerts();

    @Transaction
    @Query("SELECT * FROM course WHERE id = :id")
    LiveData<CourseWithNotes> getCourseWithNotesById(int id);

    @Transaction
    @Query("SELECT * FROM course")
    LiveData<List<CourseWithNotes>> getAllCoursesWithNotes();

    @Query("DELETE FROM course")
    void deleteAllCourses();

    @Query("SELECT COUNT(*) FROM course")
    int getAllCourseCount();
}
