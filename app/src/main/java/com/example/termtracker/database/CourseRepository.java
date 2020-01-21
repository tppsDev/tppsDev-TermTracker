package com.example.termtracker.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.termtracker.dao.CourseAlertDao;
import com.example.termtracker.dao.CourseDao;
import com.example.termtracker.dao.CourseMentorXRefDao;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.CourseAlertWithCourse;
import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.CourseWithAlerts;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.entity.CourseWithMentors;
import com.example.termtracker.entity.CourseWithNotes;
import com.example.termtracker.entity.CourseWithTerm;

import java.util.List;

public class CourseRepository {
    private CourseDao courseDao;
    private CourseAlertDao courseAlertDao;
    private CourseMentorXRefDao courseMentorXRefDao;

    private LiveData<List<Course>> allCourses;
    private LiveData<List<CourseWithTerm>> allCoursesWithTerms;
    private LiveData<List<CourseWithAssessments>> allCoursesWithCourses;
    private LiveData<List<CourseWithAlerts>> allCoursesWithAlerts;
    private LiveData<List<CourseWithNotes>> allCoursesWithNotes;
    private LiveData<List<CourseWithMentors>> allCoursesWithMentors;
    private LiveData<List<CourseAlert>> allCourseAlerts;
    private LiveData<List<CourseAlertWithCourse>> allCourseAlertsWithCourses;

    private static volatile CourseRepository INSTANCE;

    public static CourseRepository getRepository(Application application) {
        if (INSTANCE == null) {
            synchronized (CourseRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CourseRepository(application);
                }
            }
        }
        return INSTANCE;
    }
    
    private CourseRepository(Application application) {
        TermTrackerDatabase db = TermTrackerDatabase.getDatebase(application);
        courseDao = db.courseDao();
        courseAlertDao = db.courseAlertDao();
        courseMentorXRefDao = db.courseMentorXRefDao();

        allCourses = courseDao.getAllCourses();
        allCoursesWithTerms = courseDao.getAllCoursesWithTerms();
        allCoursesWithCourses = courseDao.getAllCoursesWithAssessments();
        allCoursesWithAlerts = courseDao.getAllCoursesWithAlerts();
        allCoursesWithNotes = courseDao.getAllCoursesWithNotes();
        allCourseAlerts = courseAlertDao.getAllCourseAlerts();
        allCoursesWithMentors = courseMentorXRefDao.getAllCoursesWithMentors();
        allCourseAlertsWithCourses = courseAlertDao.getAllCourseAlertsWithCourses();
    }

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<CourseWithTerm> getCourseWithTermById(int id) {
        return courseDao.getCoursewithTermById(id);
    }

    public LiveData<List<CourseWithTerm>> getAllCoursesWithTerms() {
        return allCoursesWithTerms;
    }

    public LiveData<CourseWithAssessments> getCourseWithAssessments(int id) {
        return courseDao.getCourseWithAssessmentsById(id);
    }

    public LiveData<List<CourseWithAssessments>> getAllCoursesWithAssessments() {
        return allCoursesWithCourses;
    }

    public LiveData<CourseWithAlerts> getCourseWithAlertsById(int id) {
        return courseDao.getCourseWithAlertsById(id);
    }

    public LiveData<List<CourseWithAlerts>> getAllCoursesWithAlerts() {
        return allCoursesWithAlerts;
    }

    public LiveData<CourseWithNotes> getCourseWithNotes(int id) {
        return courseDao.getCourseWithNotesById(id);
    }

    public LiveData<List<CourseWithNotes>> getAllCoursesWithNotes() {
        return allCoursesWithNotes;
    }

    public LiveData<CourseWithMentors> getCourseWithMentorsByCourseId(int courseId) {
        return courseMentorXRefDao.getCourseWithMentorsById(courseId);
    }

    public LiveData<List<CourseWithMentors>> getAllCoursesWithMentors() {
        return allCoursesWithMentors;
    }

    public LiveData<List<CourseAlert>> getAllCourseAlerts() {
        return allCourseAlerts;
    }

    public LiveData<List<CourseAlertWithCourse>> getAllCourseAlertsWithCourses() {
        return allCourseAlertsWithCourses;
    }

    public LiveData<CourseAlertWithCourse> getCourseAlertWithCourseById(int id) {
        return courseAlertDao.getCourseAlertWithCourseById(id);
    }

    public void insertCourse(Course course) {
        new CourseRepository.insertCourseAsyncTask(courseDao).execute(course);
    }

    private static class insertCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private CourseDao asyncTaskCourseDao;
        public insertCourseAsyncTask(CourseDao courseDao) {
            asyncTaskCourseDao = courseDao;
        }

        @Override
        protected Void doInBackground(Course... courses) {
            asyncTaskCourseDao.insertCourse(courses[0]);
            return null;
        }
    }

    public void updateCourse(Course course) {
        new CourseRepository.updateCourseAsyncTask(courseDao).execute(course);
    }

    private static class updateCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private CourseDao asyncTaskCourseDao;
        public updateCourseAsyncTask(CourseDao courseDao) {
            asyncTaskCourseDao = courseDao;
        }

        @Override
        protected Void doInBackground(Course... courses) {
            asyncTaskCourseDao.updateCourse(courses[0]);
            return null;
        }
    }

    public void deleteCourse(Course course) {
        new CourseRepository.deleteCourseAsyncTask(courseDao).execute(course);
    }

    private static class deleteCourseAsyncTask extends AsyncTask<Course, Void, Void> {
        private CourseDao asyncTaskCourseDao;
        public deleteCourseAsyncTask(CourseDao courseDao) {
            asyncTaskCourseDao = courseDao;
        }

        @Override
        protected Void doInBackground(Course... courses) {
            asyncTaskCourseDao.deleteCourse(courses[0]);
            return null;
        }
    }

    public void insertCourseAlert(CourseAlert courseAlert, InsertResult resultInterface) {
        new insertCourseAlertAsyncTask(courseAlertDao, resultInterface).execute(courseAlert);
    }

    private static class insertCourseAlertAsyncTask extends AsyncTask<CourseAlert, Void, Long> {
        private CourseAlertDao asyncTaskCourseAlertDao;
        private InsertResult resultInterface;

        public insertCourseAlertAsyncTask(CourseAlertDao courseAlertDao, InsertResult resultInterface) {
            asyncTaskCourseAlertDao = courseAlertDao;
            this.resultInterface = resultInterface;
        }

        @Override
        protected Long doInBackground(CourseAlert... courseAlerts) {
            return asyncTaskCourseAlertDao.insertCourseAlert(courseAlerts[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            resultInterface.handleInsertResult(result);
        }
    }

    public void updateCourseAlert(CourseAlert courseAlert) {
        new updateCourseAlertAsyncTask(courseAlertDao).execute(courseAlert);
    }

    private static class updateCourseAlertAsyncTask extends AsyncTask<CourseAlert, Void, Void> {
        private CourseAlertDao asyncTaskCourseAlertDao;
        public updateCourseAlertAsyncTask(CourseAlertDao courseAlertDao) {
            asyncTaskCourseAlertDao = courseAlertDao;
        }

        @Override
        protected Void doInBackground(CourseAlert... courseAlerts) {
            asyncTaskCourseAlertDao.updateCourseAlert(courseAlerts[0]);
            return null;
        }
    }

    public void deleteCourseAlert(CourseAlert courseAlert) {
        new deleteCourseAlertAsyncTask(courseAlertDao).execute(courseAlert);
    }

    private static class deleteCourseAlertAsyncTask extends AsyncTask<CourseAlert, Void, Void> {
        private CourseAlertDao asyncTaskCourseAlertDao;
        public deleteCourseAlertAsyncTask(CourseAlertDao courseAlertDao) {
            asyncTaskCourseAlertDao = courseAlertDao;
        }

        @Override
        protected Void doInBackground(CourseAlert... courseAlerts) {
            asyncTaskCourseAlertDao.deleteCourseAlert(courseAlerts[0]);
            return null;
        }
    }

    public void insertCourseMentorXRef(CourseMentorXRef courseMentorXRef) {
        new insertCourseMentorXRefAsyncTask(courseMentorXRefDao).execute(courseMentorXRef);
    }

    private static class insertCourseMentorXRefAsyncTask extends AsyncTask<CourseMentorXRef, Void, Void> {
        private CourseMentorXRefDao asyncTaskCourseMentorXRefDao;
        public insertCourseMentorXRefAsyncTask(CourseMentorXRefDao courseMentorXRefDao) {
            asyncTaskCourseMentorXRefDao = courseMentorXRefDao;
        }

        @Override
        protected Void doInBackground(CourseMentorXRef... courseMentorXRefs) {
            asyncTaskCourseMentorXRefDao.insertCourseMentorRelation(courseMentorXRefs[0]);
            return null;
        }
    }

    public void deleteCourseMentorXRef(CourseMentorXRef courseMentorXRef) {
        new deleteCourseMentorXRefAsyncTask(courseMentorXRefDao).execute(courseMentorXRef);
    }

    private static class deleteCourseMentorXRefAsyncTask extends AsyncTask<CourseMentorXRef, Void, Void> {
        private CourseMentorXRefDao asyncTaskCourseMentorXRefDao;
        public deleteCourseMentorXRefAsyncTask(CourseMentorXRefDao courseMentorXRefDao) {
            asyncTaskCourseMentorXRefDao = courseMentorXRefDao;
        }

        @Override
        protected Void doInBackground(CourseMentorXRef... courseMentorXRefs) {
            asyncTaskCourseMentorXRefDao.deleteCourseMentorRelation(courseMentorXRefs[0]);
            return null;
        }
    }
}
