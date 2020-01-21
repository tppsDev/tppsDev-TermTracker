package com.example.termtracker.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.termtracker.entity.Term;
import com.example.termtracker.entity.TermWithCourses;
import com.example.termtracker.entity.TermWithCoursesWithAssessments;

import java.util.List;

@Dao
public interface TermDao {
    // CRUD methods
    @Insert
    void insertTerm(Term term);

    @Insert
    void insertAllTerms(List<Term> terms);

    @Update
    void updateTerm(Term term);

    @Delete
    void deleteTerm(Term term);

    @Query("SELECT * FROM term WHERE id = :id")
    Term getTermById(int id);

    @Query("SELECT * FROM term ORDER BY startDate")
    LiveData<List<Term>> getAllTerms();

    @Transaction
    @Query("SELECT * FROM term WHERE id = :id")
    LiveData<TermWithCourses> getTermWithCoursesById(int id);

    @Transaction
    @Query("SELECT * FROM term")
    LiveData<List<TermWithCourses>> getAllTermsWithCourses();

    @Transaction
    @Query("SELECT * FROM term WHERE id = :id")
    LiveData<TermWithCoursesWithAssessments> getTermWithCoursesWithAssessmentsById(int id);

    @Transaction
    @Query("SELECT * FROM term WHERE startDate <= :now AND endDate >= :now")
    LiveData<TermWithCoursesWithAssessments> getCurrentTermWithCoursesWithAssessments(long now);

    @Transaction
    @Query("SELECT * FROM term")
    LiveData<List<TermWithCoursesWithAssessments>> getAllTermsWithCoursesWithAssessments();

    @Query("SELECT COUNT(*) FROM course WHERE termId = :termId")
    int getTermCourseCount(int termId);

    @Query("DELETE FROM term")
    void deleteAllTerms();

    @Query("SELECT COUNT(*) FROM term")
    int getAllTermCount();


}
