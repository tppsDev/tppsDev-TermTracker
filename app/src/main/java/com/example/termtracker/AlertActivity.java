package com.example.termtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.AssessmentAlertWithAssessment;
import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.CourseAlertWithCourse;
import com.example.termtracker.ui.AssessmentAlertWithAssessmentListAdapter;
import com.example.termtracker.ui.CourseAlertWithCourseListAdapter;
import com.example.termtracker.util.AlertReceiver;
import com.example.termtracker.view_model.AllAlertsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class AlertActivity extends AppCompatActivity {
    private static final int EDIT_ASSESSMENT_ALERT_REQ_CODE = 24;
    private static final int EDIT_COURSE_ALERT_REQ_CODE = 25;
    AllAlertsViewModel viewModel;
    AssessmentAlertWithAssessmentListAdapter assessmentAlertAdapter;
    CourseAlertWithCourseListAdapter courseAlertAdapter;
    ActionMenuView menuView;
    TextView alertSubHeaderTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        viewModel = ViewModelProviders.of(this).get(AllAlertsViewModel.class);

        Toolbar appBar = findViewById(R.id.alertTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        SharedPreferences sPref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        alertSubHeaderTextView = findViewById(R.id.alertSubHeaderTextView);
        String user = sPref.getString(getString(R.string.user_name_key), "User");
        user = user + "'s Alerts";
        alertSubHeaderTextView.setText(user);

        RecyclerView assessmentAlertRV = findViewById(R.id.alertAssessmentAlertRV);
        assessmentAlertAdapter = new AssessmentAlertWithAssessmentListAdapter(this, assessmentAlertListener);
        assessmentAlertRV.setAdapter(assessmentAlertAdapter);
        assessmentAlertRV.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView courseAlertRV = findViewById(R.id.alertCourseAlertRV);
        courseAlertAdapter = new CourseAlertWithCourseListAdapter(this, courseAlertListener);
        courseAlertRV.setAdapter(courseAlertAdapter);
        courseAlertRV.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getAllAssessmentAlertsWithAssessments().observe(this, new Observer<List<AssessmentAlertWithAssessment>>() {
            @Override
            public void onChanged(List<AssessmentAlertWithAssessment> assessmentAlertWithAssessments) {
                assessmentAlertAdapter.setAssessmentAlerts(assessmentAlertWithAssessments);
            }
        });

        viewModel.getAllCourseAlertsWithCourses().observe(this, new Observer<List<CourseAlertWithCourse>>() {
            @Override
            public void onChanged(List<CourseAlertWithCourse> courseAlertWithCourses) {
                courseAlertAdapter.setCourseAlerts(courseAlertWithCourses);
            }
        });

        final ColorDrawable background = new ColorDrawable(ContextCompat.getColor(this, R.color.colorErrorRed));
        Drawable deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_36);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 0;
                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());


                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                deleteIcon.setBounds(iconLeft,iconTop,iconRight,iconBottom);

                background.draw(c);
                deleteIcon.draw(c);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AssessmentAlert selectedAlert = assessmentAlertAdapter.getAssessmentAlertAt(viewHolder.getAdapterPosition());

                viewModel.deleteAssessmentAlert(selectedAlert);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    Intent alertReceiverIntent = new Intent(getApplicationContext(), AlertReceiver.class);
                    alertReceiverIntent
                            .putExtra(getString(R.string.alert_title_key), getString(R.string.termtracker_assessment_alert));
                    alertReceiverIntent
                            .putExtra(getString(R.string.alert_message_key), selectedAlert.getAlertMessage());
                    alertReceiverIntent.putExtra(getString(R.string.alert_id_key), selectedAlert.getId());
                    Log.d("Timmy Time", "onSwiped: " + selectedAlert.getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(),
                            selectedAlert.getId(),
                            alertReceiverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    ZonedDateTime zDateTime  = ZonedDateTime.of(selectedAlert.getAlertTime(), ZoneId.systemDefault());
                    long alertTime = zDateTime.toInstant().toEpochMilli();
                    alarmManager.cancel(pendingIntent);
                }else {
                    Snackbar snackbar = Snackbar.make(menuView.getRootView(),
                            getString(R.string.no_alarm_mgr_error),
                            5000);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    snackbar.show();
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(assessmentAlertRV);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 0;
                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());


                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                deleteIcon.setBounds(iconLeft,iconTop,iconRight,iconBottom);

                background.draw(c);
                deleteIcon.draw(c);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                CourseAlert selectedAlert = courseAlertAdapter.getCourseAlertAt(viewHolder.getAdapterPosition());

                viewModel.deleteCourseAlert(selectedAlert);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    Intent alertReceiverIntent = new Intent(getApplicationContext(), AlertReceiver.class);
                    alertReceiverIntent
                            .putExtra(getString(R.string.alert_title_key), getString(R.string.termtracker_course_alert));
                    alertReceiverIntent
                            .putExtra(getString(R.string.alert_message_key), selectedAlert.getAlertMessage());
                    alertReceiverIntent.putExtra(getString(R.string.alert_id_key), selectedAlert.getId());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            getApplicationContext(),
                            // Subtract key from zero to ensure course alerts and assessment alerts can use the same receiver
                            0 - selectedAlert.getId(),
                            alertReceiverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.d("Timmy Time", "onSwiped: "+ (0 - selectedAlert.getId()));
                    alarmManager.cancel(pendingIntent);
                }else {
                    Snackbar snackbar = Snackbar.make(menuView.getRootView(),
                            getString(R.string.no_alarm_mgr_error),
                            5000);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    snackbar.show();
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(courseAlertRV);

    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( AlertActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(AlertActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(AlertActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(AlertActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                break;
        }
        return true;
    }

    private AssessmentAlertWithAssessmentListAdapter.OnItemClickListener assessmentAlertListener = new AssessmentAlertWithAssessmentListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(AssessmentAlertWithAssessment alert) {
            Intent assessmentAlertIntent = new Intent(AlertActivity.this, AssessmentAlertEditActivity.class);
            assessmentAlertIntent.putExtra(getString(R.string.edit_assessment_alert_mode_key), 0);
            assessmentAlertIntent.putExtra(getString(R.string.assessment_alert_object_key), alert);
            assessmentAlertIntent.putExtra(getString(R.string.assessment_object_key), alert.getAssessment());
            startActivityForResult(assessmentAlertIntent, EDIT_ASSESSMENT_ALERT_REQ_CODE);
        }
    };

    private CourseAlertWithCourseListAdapter.OnItemClickListener courseAlertListener = new CourseAlertWithCourseListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(CourseAlertWithCourse alert) {
            Intent courseAlertIntent = new Intent(AlertActivity.this, CourseAlertEditActivity.class);
            courseAlertIntent.putExtra(getString(R.string.course_alert_object_key), alert);
            courseAlertIntent.putExtra(getString(R.string.course_object_key), alert.getCourse());
            courseAlertIntent.putExtra(getString(R.string.edit_course_alert_mode_key), 0);
            startActivityForResult(courseAlertIntent, EDIT_COURSE_ALERT_REQ_CODE);
        }
    };
}
