package com.example.termtracker.dao;

import androidx.annotation.TransitionRes;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.CourseWithMentors;

import java.util.List;

@Dao
public interface CourseMentorXRefDao {
    // Queries for many to many junction table between course and mentor
    @Transaction
    @Query("SELECT * FROM  course")
    LiveData<List<CourseWithMentors>> getAllCoursesWithMentors();

    @Transaction
    @Query("SELECT * FROM course WHERE id = :id")
    LiveData<CourseWithMentors> getCourseWithMentorsById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourseMentorRelation(CourseMentorXRef courseMentorXRef);

    @Delete
    void deleteCourseMentorRelation(CourseMentorXRef courseMentorXRef);
}
