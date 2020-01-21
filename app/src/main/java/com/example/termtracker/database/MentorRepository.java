package com.example.termtracker.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.termtracker.dao.MentorDao;
import com.example.termtracker.entity.Mentor;

import java.util.List;

public class MentorRepository {
    private MentorDao mentorDao;

    private LiveData<List<Mentor>> allMentors;

    private static volatile MentorRepository INSTANCE;

    public static MentorRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (MentorRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MentorRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private MentorRepository(Application application) {
        TermTrackerDatabase db = TermTrackerDatabase.getDatebase(application);
        mentorDao = db.mentorDao();
        allMentors = mentorDao.getAllMentors();

    }

    public LiveData<Mentor> getMentorById(int id) {
        return mentorDao.getMentorById(id);
    }

    public LiveData<List<Mentor>> getAllMentors() {
        return allMentors;
    }

    public void insertMentor(Mentor mentor) {
        new insertMentorAsyncTask(mentorDao).execute(mentor);
    }

    private static class insertMentorAsyncTask extends AsyncTask<Mentor, Void, Void> {
        private MentorDao asyncTaskMentorDao;
        public insertMentorAsyncTask(MentorDao mentorDao) {
            asyncTaskMentorDao = mentorDao;
        }

        @Override
        protected Void doInBackground(Mentor... mentors) {
            asyncTaskMentorDao.insertMentor(mentors[0]);
            return null;
        }
    }

    public void getCourseCountByMentorId(int mentorId, ConstraintCheck constraintCheck) {
        new getCourseCountByMentorIdAsyncTask(mentorDao, constraintCheck).execute(mentorId);
    }

    private static class getCourseCountByMentorIdAsyncTask extends AsyncTask<Integer, Void, Integer> {
        private MentorDao asyncMentorDao;
        private ConstraintCheck constraintCheck;

        public getCourseCountByMentorIdAsyncTask(MentorDao mentorDao, ConstraintCheck constraintCheck) {
            this.asyncMentorDao = mentorDao;
            this.constraintCheck = constraintCheck;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            return asyncMentorDao.getCourseCountByMentorId(integers[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            constraintCheck.handleConstraintCheck(integer);
        }
    }

    public void updateMentor(Mentor mentor) {
        new updateMentorAsyncTask(mentorDao).execute(mentor);
    }

    private static class updateMentorAsyncTask extends AsyncTask<Mentor, Void, Void> {
        private MentorDao asyncTaskMentorDao;
        public updateMentorAsyncTask(MentorDao mentorDao) {
            asyncTaskMentorDao = mentorDao;
        }

        @Override
        protected Void doInBackground(Mentor... mentors) {
            asyncTaskMentorDao.updateMentor(mentors[0]);
            return null;
        }
    }

    public void deleteMentor(Mentor mentor) {
        new deleteMentorAsyncTask(mentorDao).execute(mentor);
    }

    private static class deleteMentorAsyncTask extends AsyncTask<Mentor, Void, Void> {
        private MentorDao asyncTaskMentorDao;
        public deleteMentorAsyncTask(MentorDao mentorDao) {
            asyncTaskMentorDao = mentorDao;
        }

        @Override
        protected Void doInBackground(Mentor... mentors) {
            asyncTaskMentorDao.deleteMentor(mentors[0]);
            return null;
        }
    }
}
