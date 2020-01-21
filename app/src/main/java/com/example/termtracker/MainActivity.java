package com.example.termtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.Assessment.AssessmentStatus;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.Course.CourseStatus;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.entity.TermWithCoursesWithAssessments;
import com.example.termtracker.ui.AssessmentWithCourseNameListAdapter;
import com.example.termtracker.ui.CourseWithAssessmentsListAdapter;
import com.example.termtracker.util.DateTimeUtilException;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.util.PeriodResponse;
import com.example.termtracker.view_model.HomeViewModel;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int TERM_TRACKER_REQ_CODE = 3;
    ActionMenuView menuView;

    private CourseWithAssessmentsListAdapter courseWithAssessmentsListAdapter;
    private AssessmentWithCourseNameListAdapter assessmentWithCourseNameListAdapter;
    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        homeViewModel = ViewModelProviders.of(this)
                .get(HomeViewModel.class);
        Toolbar appBar = findViewById(R.id.termTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        RecyclerView courseRecyclerView = findViewById(R.id.mainCourseRecycleView);
        courseWithAssessmentsListAdapter =
                new CourseWithAssessmentsListAdapter(this, courseItemClickListener);
        courseRecyclerView.setAdapter(courseWithAssessmentsListAdapter);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView assessmentsRecyclerView = findViewById(R.id.mainAssessmentRecyclerView);
        assessmentWithCourseNameListAdapter =
                new AssessmentWithCourseNameListAdapter(this, item ->
                        handleAssessmentClick(item));
        assessmentsRecyclerView.setAdapter(assessmentWithCourseNameListAdapter);
        assessmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        assessmentsRecyclerView.setNestedScrollingEnabled(false);

        homeViewModel.getCurrentTermWithCoursesWithAssessments().observe(this, new Observer<TermWithCoursesWithAssessments>() {
            @Override
            public void onChanged(TermWithCoursesWithAssessments termWithCoursesWithAssessments) {
                updateDashBoard();
            }
        });

        homeViewModel.getAllCoursesWithAssessments().observe(this, new Observer<List<CourseWithAssessments>>() {
            @Override
            public void onChanged(List<CourseWithAssessments> courseWithAssessments) {
                courseWithAssessmentsListAdapter.setCourses(courseWithAssessments);
            }
        });

        homeViewModel.getAllAssessmentsWithCourseName().observe(this, new Observer<List<AssessmentWithCourse>>() {
            @Override
            public void onChanged(List<AssessmentWithCourse> assessmentWithCourses) {
                assessmentWithCourseNameListAdapter.setAssessments(assessmentWithCourses);
            }
        });
        if (isFirstRun(this, this.getString(R.string.database_name))) {
            // Display Setup User
            Intent intent = new Intent(MainActivity.this, UserSetupActivity.class);
            startActivity(intent);
        }
        updateDashBoard();
    }

    private CourseWithAssessmentsListAdapter.OnItemClickListener courseItemClickListener =
            new CourseWithAssessmentsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Course course) {
                    Intent courseDetailIntent = new Intent(MainActivity.this, CourseDetailActivity.class);
                    courseDetailIntent.putExtra("COURSE_ID", course.getId());
                    startActivity(courseDetailIntent);
                }
            };

    private void handleCourseItemClick(Course course) {
        Intent courseDetailIntent = new Intent(MainActivity.this, CourseDetailActivity.class);
        courseDetailIntent.putExtra("COURSE_ID", course.getId());
        startActivity(courseDetailIntent);
    }

    private void handleAssessmentClick(Assessment assessment) {
        Intent assessmentDetailIntent =
                new Intent(MainActivity.this, AssessmentDetailActivity.class);
        assessmentDetailIntent.putExtra("ASSESSMENT_ID", assessment.getId());
        startActivity(assessmentDetailIntent);
    }

    private void updateDashBoard() {
        TextView currentTermTextView;
        TextView periodRemainingTextView;
        TextView periodRemainingLabelTextView;
        TextView coursesRemainingTextView;
        TextView assessmentsRemainingTextView;
        TermWithCoursesWithAssessments currentTerm = homeViewModel.getCurrentTermWithCoursesWithAssessments().getValue();
        PeriodResponse periodRemaining;

        currentTermTextView = this.findViewById(R.id.currentTermTextView);
        periodRemainingTextView = this.findViewById(R.id.periodRemainingTextView);
        periodRemainingLabelTextView = this.findViewById(R.id.periodRemainingLabelTextView);
        coursesRemainingTextView = this.findViewById(R.id.coursesRemainingTextView);
        assessmentsRemainingTextView = this.findViewById(R.id.assessmentsRemainingTextView);

        if (currentTerm != null) {
            currentTermTextView.setText(currentTerm.getTerm().getTitle());
            try {
                periodRemaining = DateTimeUtils.periodUntil(currentTerm.getTerm().getEndDate());

                switch (periodRemaining.getPeriodUnit()) {
                    case DAY:
                        periodRemainingLabelTextView.setText(R.string.days_remaining);
                        break;
                    case WEEK:
                        periodRemainingLabelTextView.setText(R.string.weeks_remaining);
                        break;
                    case MONTH:
                        periodRemainingLabelTextView.setText(R.string.months_remaining);
                        break;
                }

                periodRemainingTextView.setText(String.valueOf(periodRemaining.getPeriodInt()));

            } catch (DateTimeUtilException e) {
                e.printStackTrace();
            }
            int courseCount = 0;
            int assessmentCount = 0;
            for (CourseWithAssessments course : currentTerm.getCourses()) {
                if (course.getCourse().getStatus().equals(CourseStatus.IN_PROGRESS)
                        || course.getCourse().getStatus().equals(CourseStatus.PLAN_TO_TAKE)) {
                    courseCount++;
                    for (Assessment assessment : course.getAssessments()) {
                        if (!assessment.getStatus().equals(AssessmentStatus.PASSED)) {
                            assessmentCount++;
                        }
                    }
                }
            }

            coursesRemainingTextView.setText(String.valueOf(courseCount));
            assessmentsRemainingTextView.setText(String.valueOf(assessmentCount));
        }
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.termItem:
                Intent termIntent = new Intent(MainActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(MainActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(MainActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(MainActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    private static boolean isFirstRun(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return !dbFile.exists();
    }


}
