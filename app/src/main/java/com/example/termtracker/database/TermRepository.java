package com.example.termtracker.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.termtracker.dao.TermDao;
import com.example.termtracker.entity.Term;
import com.example.termtracker.entity.TermWithCourses;
import com.example.termtracker.entity.TermWithCoursesWithAssessments;

import java.time.Instant;
import java.util.List;

public class TermRepository {
    private TermDao termDao;

    private LiveData<List<Term>> allTerms;
    private LiveData<List<TermWithCourses>> allTermsWithCourses;
    private LiveData<List<TermWithCoursesWithAssessments>> allTermsWithCoursesWithAssessments;

    private static volatile TermRepository INSTANCE;

    public static TermRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (TermRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TermRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private TermRepository(Application application) {
        TermTrackerDatabase db = TermTrackerDatabase.getDatebase(application);
        termDao = db.termDao();


        allTerms = termDao.getAllTerms();
        allTermsWithCourses = termDao.getAllTermsWithCourses();
        allTermsWithCoursesWithAssessments = termDao.getAllTermsWithCoursesWithAssessments();
    }


    public LiveData<List<Term>> getAllTerms() {
        return allTerms;
    }

    public LiveData<List<TermWithCourses>> getAllTermsWithCourses() {
        return allTermsWithCourses;
    }

    public LiveData<TermWithCourses> getTermWithCourses(int id) {
        return termDao.getTermWithCoursesById(id);
    }

    public LiveData<TermWithCoursesWithAssessments> getTermWithCoursesWithAssessmentsById(int id) {
        return termDao.getTermWithCoursesWithAssessmentsById(id);
    }

    public LiveData<TermWithCoursesWithAssessments> getCurrentTermWithCoursesWithAssessments() {
        Instant now = Instant.now();
        return termDao.getCurrentTermWithCoursesWithAssessments(now.toEpochMilli());
    }

    public int getTermsCourseCount(int termId) {
        return termDao.getTermCourseCount(termId);
    }

    public LiveData<List<TermWithCoursesWithAssessments>> getAllTermsWithCoursesWithAssessments() {
        return allTermsWithCoursesWithAssessments;
    }

    public void insertTerm(Term term) {
        new insertTermAsyncTask(termDao).execute(term);
    }


    private static class insertTermAsyncTask extends AsyncTask<Term, Void, Void> {
        private TermDao asyncTaskTermDao;
        public insertTermAsyncTask(TermDao termDao) {
            asyncTaskTermDao = termDao;
        }

        @Override
        protected Void doInBackground(Term... terms) {
            asyncTaskTermDao.insertTerm(terms[0]);
            return null;
        }
    }

    public void updateTerm(Term term) {
        new updateTermAsyncTask(termDao).execute(term);
    }

    private static class updateTermAsyncTask extends AsyncTask<Term, Void, Void> {
        private TermDao asyncTaskTermDao;
        public updateTermAsyncTask(TermDao termDao) {
            asyncTaskTermDao = termDao;
        }

        @Override
        protected Void doInBackground(Term... terms) {
            asyncTaskTermDao.updateTerm(terms[0]);
            return null;
        }
    }

    public void deleteTerm(Term term) {
        new deleteTermAsyncTask(termDao).execute(term);
    }

    private static class deleteTermAsyncTask extends AsyncTask<Term, Void, Void> {
        private TermDao asyncTaskTermDao;
        public deleteTermAsyncTask(TermDao termDao) {
            asyncTaskTermDao = termDao;
        }

        @Override
        protected Void doInBackground(Term... terms) {
            asyncTaskTermDao.deleteTerm(terms[0]);
            return null;
        }
    }
}
