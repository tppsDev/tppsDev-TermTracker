package com.example.termtracker.database;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.termtracker.R;
import com.example.termtracker.dao.AssessmentAlertDao;
import com.example.termtracker.dao.AssessmentDao;
import com.example.termtracker.dao.CourseAlertDao;
import com.example.termtracker.dao.CourseDao;
import com.example.termtracker.dao.CourseMentorXRefDao;
import com.example.termtracker.dao.MentorDao;
import com.example.termtracker.dao.NoteDao;
import com.example.termtracker.dao.TermDao;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.CourseMentorXRef;
import com.example.termtracker.entity.Mentor;
import com.example.termtracker.entity.Note;
import com.example.termtracker.entity.Term;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Database(entities = {Assessment.class,
                      AssessmentAlert.class,
                      Course.class,
                      CourseAlert.class,
                      CourseMentorXRef.class,
                      Mentor.class,
                      Note.class,
                      Term.class},
        version = 1,
        exportSchema = false)
@TypeConverters({
        AssessmentStatusConverter.class,
        AssessmentTypeConverter.class,
        CourseStatusConverter.class,
        LocalDateConverter.class,
        LocalDateTimeConverter.class
})
abstract class TermTrackerDatabase extends RoomDatabase {
    abstract AssessmentDao assessmentDao();
    abstract AssessmentAlertDao assessmentAlertDao();
    abstract CourseDao courseDao();
    abstract CourseAlertDao courseAlertDao();
    abstract CourseMentorXRefDao courseMentorXRefDao();
    abstract MentorDao mentorDao();
    abstract NoteDao noteDao();
    abstract TermDao termDao();

    private static volatile TermTrackerDatabase INSTANCE;

    static TermTrackerDatabase getDatebase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TermTrackerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TermTrackerDatabase.class, context.getString(R.string.database_name))
                            //.addCallback(databaseCallBack)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback databaseCallBack =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final TermDao termDao;
        private final CourseDao courseDao;
        private final AssessmentDao assessmentDao;

        public PopulateDbAsync(TermTrackerDatabase db) {
            termDao = db.termDao();
            courseDao = db.courseDao();
            assessmentDao = db.assessmentDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            assessmentDao.deleteAllAssessments();
            courseDao.deleteAllCourses();
            termDao.deleteAllTerms();

            Term term = new Term(101,"Test Term 1",
                    LocalDate.of(2019, 11, 1),
                    LocalDate.of(2020,5,31));
            termDao.insertTerm(term);

            Course course = new Course(1001, 101, "Intro to Underwater Basketweaving",
                    LocalDate.of(2019,11,1),
                    LocalDate.of(2019,12,15), Course.CourseStatus.IN_PROGRESS);
            courseDao.insertCourse(course);

            course = new Course(1002, 101, "Applied Board Game Strategy",
                    LocalDate.of(2019,11,1),
                    LocalDate.of(2019,11,30), Course.CourseStatus.IN_PROGRESS);
            courseDao.insertCourse(course);

            Assessment assessment = new Assessment(10001,
                    Assessment.AssessmentType.OBJECTIVE,
                    1001,
                    "Task 1: A5TE",
                    "Exam covering the learning materials",
                    LocalDate.of(2019,12,15),
                    Assessment.AssessmentStatus.NOT_SCHEDULED);
            assessmentDao.insertAssessment(assessment);

            assessment = new Assessment(10002,
                    Assessment.AssessmentType.OBJECTIVE,
                    1002,
                    "Assessment: B5ZE",
                    "Exam covering the learning materials",
                    LocalDate.of(2019,11,30),
                    Assessment.AssessmentStatus.NOT_SCHEDULED);
            assessmentDao.insertAssessment(assessment);

            return null;
        }
    }
}
