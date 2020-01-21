package com.example.termtracker.database;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.termtracker.dao.AssessmentAlertDao;
import com.example.termtracker.dao.AssessmentDao;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.AssessmentAlertWithAssessment;
import com.example.termtracker.entity.AssessmentWithAlerts;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.AssessmentWithNotes;

import java.util.List;

public class AssessmentRepository {
    private AssessmentDao assessmentDao;
    private AssessmentAlertDao assessmentAlertDao;

    private LiveData<List<Assessment>> allAssessments;
    private LiveData<List<AssessmentWithCourse>> allAssessmentsWithCourse;
    private LiveData<List<AssessmentWithAlerts>> allAssessmentsWithAlerts;
    private LiveData<List<AssessmentAlert>> allAssessmentAlerts;
    private LiveData<List<AssessmentWithNotes>> allAssessmentsWithNotes;
    private LiveData<List<AssessmentAlertWithAssessment>> allAssessmentAlertsWithAssessments;

    private static volatile AssessmentRepository INSTANCE;

    public static AssessmentRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (AssessmentRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AssessmentRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    private AssessmentRepository(Application application) {
        TermTrackerDatabase db = TermTrackerDatabase.getDatebase(application);
        assessmentDao = db.assessmentDao();
        assessmentAlertDao = db.assessmentAlertDao();

        allAssessments = assessmentDao.getAllAssessments();
        allAssessmentsWithCourse = assessmentDao.getAssessmentsWithCourse();
        allAssessmentsWithAlerts = assessmentDao.getAllAssessmentsWithAlerts();
        allAssessmentsWithNotes = assessmentDao.getAllAssessmentsWithNotes();
        allAssessmentAlerts = assessmentAlertDao.getAllAssessmentAlerts();
        allAssessmentAlertsWithAssessments = assessmentAlertDao.getAllAssessmentAlertsWithAssessments();
    }

    public LiveData<List<Assessment>> getAllAssessments() {
        return allAssessments;
    }

    public LiveData<List<AssessmentWithCourse>> getAllAssessmentsWithCourse() {
        return allAssessmentsWithCourse;
    }

    public LiveData<AssessmentWithCourse> getAssessmentWithCourseById(int id) {
        return assessmentDao.getAssessmentsWithCourseById(id);
    }

    public LiveData<List<AssessmentWithAlerts>> getAllAssessmentsWithAlerts() {
        return allAssessmentsWithAlerts;
    }

    public LiveData<AssessmentWithAlerts> getAssessmentWithAlertsById(int id) {
        return assessmentDao.getAssessmentWithAlertsById(id);
    }

    public LiveData<List<AssessmentWithNotes>> getAllAssessmentsWithNotes() {
        return allAssessmentsWithNotes;
    }

    public LiveData<AssessmentWithNotes> getAssessmentWithNotesById(int id) {
        return assessmentDao.getAssessmentWithNotesById(id);
    }

    public LiveData<List<AssessmentAlert>> getAllAssessmentAlerts() {
        return allAssessmentAlerts;
    }

    public LiveData<List<AssessmentAlertWithAssessment>> getAllAssessmentAlertsWithAssessments() {
        return allAssessmentAlertsWithAssessments;
    }

    public LiveData<AssessmentAlertWithAssessment> getAssessmentAlertWithAssessmentById(int id) {
        return assessmentAlertDao.getAssessmentAlertWithAssessmentById(id);
    }

    public void getAlertCountByAssessmentId(int assessmentID, ConstraintCheck constraintCheck) {
        new getAlertCountByAssessmentIdAsyncTask(assessmentDao, constraintCheck).execute(assessmentID);
    }

    private static class getAlertCountByAssessmentIdAsyncTask extends AsyncTask<Integer, Void, Integer> {
        private AssessmentDao asyncAssessmentDao;
        private ConstraintCheck constraintCheck;

        public getAlertCountByAssessmentIdAsyncTask(AssessmentDao assessmentDao, ConstraintCheck constraintCheck) {
            asyncAssessmentDao = assessmentDao;
            this.constraintCheck = constraintCheck;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            return asyncAssessmentDao.getAlertCountByAssessmentId(integers[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            constraintCheck.handleConstraintCheck(integer);
        }
    }

    public void insertAssessment(Assessment assessment) {
        new insertAssessmentAsyncTask(assessmentDao).execute(assessment);
    }

    private static class insertAssessmentAsyncTask extends AsyncTask<Assessment, Void, Void> {
        private AssessmentDao asyncTaskAssessmentDao;
        public insertAssessmentAsyncTask(AssessmentDao assessmentDao) {
            asyncTaskAssessmentDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Assessment... assessments) {
            asyncTaskAssessmentDao.insertAssessment(assessments[0]);
            return null;
        }
    }

    public void updateAssessment(Assessment assessment) {
        new updateAssessmentAsyncTask(assessmentDao).execute(assessment);
    }

    private static class updateAssessmentAsyncTask extends AsyncTask<Assessment, Void, Void> {
        private AssessmentDao asyncTaskAssessmentDao;
        public updateAssessmentAsyncTask(AssessmentDao assessmentDao) {
            asyncTaskAssessmentDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Assessment... assessments) {
            asyncTaskAssessmentDao.updateAssessment(assessments[0]);
            return null;
        }
    }

    public void deleteAssessment(Assessment assessment) {
        new deleteAssessmentAsyncTask(assessmentDao).execute(assessment);
    }

    private static class deleteAssessmentAsyncTask extends AsyncTask<Assessment, Void, Void> {
        private AssessmentDao asyncTaskAssessmentDao;
        public deleteAssessmentAsyncTask(AssessmentDao assessmentDao) {
            asyncTaskAssessmentDao = assessmentDao;
        }

        @Override
        protected Void doInBackground(Assessment... assessments)  {
            asyncTaskAssessmentDao.deleteAssessment(assessments[0]);
            return null;
        }
    }

    public void insertAssessmentAlert(AssessmentAlert assessmentAlert, InsertResult resultInterface) {
         new InsertAssessmentAlertAsyncTask(assessmentAlertDao, resultInterface).execute(assessmentAlert);
    }

    private static class InsertAssessmentAlertAsyncTask extends AsyncTask<AssessmentAlert, Void, Long> {
        private AssessmentAlertDao asyncTaskAssessmentAlertDao;
        private InsertResult resultInterface;

        public InsertAssessmentAlertAsyncTask(AssessmentAlertDao assessmentAlertDao, InsertResult resultInterface) {
            asyncTaskAssessmentAlertDao = assessmentAlertDao;
            this.resultInterface = resultInterface;
        }

        @Override
        protected Long doInBackground(AssessmentAlert... assessmentAlerts) {
            return asyncTaskAssessmentAlertDao.insertAssessmentAlert(assessmentAlerts[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            resultInterface.handleInsertResult(result);
        }
    }

    public void updateAssessmentAlert(AssessmentAlert assessmentAlert) {
        new updateAssessmentAlertAsyncTask(assessmentAlertDao).execute(assessmentAlert);
    }

    private static class updateAssessmentAlertAsyncTask extends AsyncTask<AssessmentAlert, Void, Void> {
        private AssessmentAlertDao asyncTaskAssessmentAlertDao;

        public updateAssessmentAlertAsyncTask(AssessmentAlertDao assessmentAlertDao) {
            asyncTaskAssessmentAlertDao = assessmentAlertDao;
        }

        @Override
        protected Void doInBackground(AssessmentAlert... assessmentAlerts) {
            asyncTaskAssessmentAlertDao.updateAssessmentAlert(assessmentAlerts[0]);
            return null;
        }

    }

    public void deleteAssessmentAlert(AssessmentAlert assessmentAlert) {
        new deleteAssessmentAlertAsyncTask(assessmentAlertDao).execute(assessmentAlert);
    }

    private static class deleteAssessmentAlertAsyncTask extends AsyncTask<AssessmentAlert, Void, Void> {
        private AssessmentAlertDao asyncTaskAssessmentAlertDao;
        public deleteAssessmentAlertAsyncTask(AssessmentAlertDao assessmentAlertDao) {
            asyncTaskAssessmentAlertDao = assessmentAlertDao;
        }

        @Override
        protected Void doInBackground(AssessmentAlert... assessmentAlerts) {
            asyncTaskAssessmentAlertDao.deleteAssessmentAlert(assessmentAlerts[0]);
            return null;
        }
    }
}
