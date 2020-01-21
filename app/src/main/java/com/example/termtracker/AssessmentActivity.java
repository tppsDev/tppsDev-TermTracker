package com.example.termtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.ui.AssessmentWithCourseNameListAdapter;
import com.example.termtracker.view_model.AllAssessmentsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AssessmentActivity extends AppCompatActivity implements ConstraintCheck {
    private static final int ADD_ASSESSMENT_REQ_CODE = 19;

    ActionMenuView menuView;
    TextView subHeaderTextView;
    FloatingActionButton addAssessmentFab;
    AssessmentWithCourseNameListAdapter assessmentAdapter;
    AllAssessmentsViewModel assessmentViewModel;
    Assessment selectedAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        assessmentViewModel = ViewModelProviders.of(this).get(AllAssessmentsViewModel.class);

        Toolbar appBar = findViewById(R.id.assessmentTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        SharedPreferences sPref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        subHeaderTextView = findViewById(R.id.assessmentSubHeaderTextView);
        String user = sPref.getString(getString(R.string.user_name_key), "User");
        user = user + "'s Assessments";
        subHeaderTextView.setText(user);

        RecyclerView assessmentRV = findViewById(R.id.assessmentRV);
        assessmentAdapter = new AssessmentWithCourseNameListAdapter(this,
                new AssessmentWithCourseNameListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Assessment item) {
                        handleAssessmentItemClicked(item);
                    }
                });
        assessmentRV.setAdapter(assessmentAdapter);
        assessmentRV.setLayoutManager(new LinearLayoutManager(this));

        assessmentViewModel.getAllAssessmentsWithCourses().observe(this, new Observer<List<AssessmentWithCourse>>() {
            @Override
            public void onChanged(List<AssessmentWithCourse> assessmentWithCourses) {
                assessmentAdapter.setAssessments(assessmentWithCourses);
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
                int assessmentId = assessmentAdapter
                        .getAssessmentAt(viewHolder.getAdapterPosition())
                        .getAssessment()
                        .getId();
                selectedAssessment =
                        assessmentAdapter.getAssessmentAt(viewHolder.getAdapterPosition()).getAssessment();

                assessmentViewModel.getAlertCountByAssessmentId(assessmentId, AssessmentActivity.this);

            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(assessmentRV);

        addAssessmentFab = findViewById(R.id.assessmentAddAssessmentFab);
        addAssessmentFab.setOnClickListener(v -> handleAddAssessmentFab());
    }

    private void handleAssessmentItemClicked(Assessment assessment) {
        Intent assessmentDetailIntent =
                new Intent(AssessmentActivity.this, AssessmentDetailActivity.class);
        assessmentDetailIntent.putExtra("ASSESSMENT_ID", assessment.getId());
        startActivity(assessmentDetailIntent);
    }

    private void handleAddAssessmentFab() {
        Intent editAssessmentIntent =
                new Intent(AssessmentActivity.this, AssessmentEditActivity.class);
        editAssessmentIntent.putExtra(getString(R.string.edit_assessment_mode_key), 1);
        startActivityForResult(editAssessmentIntent, ADD_ASSESSMENT_REQ_CODE);
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( AssessmentActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(AssessmentActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(AssessmentActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(AssessmentActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ASSESSMENT_REQ_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Assessment Added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Add Assessment cancelled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void handleConstraintCheck(int count) {
        if (count < 1) {
            assessmentViewModel.deleteAssessment(selectedAssessment);
        } else {
            Snackbar snackbar = Snackbar.make(addAssessmentFab,
                    getString(R.string.delete_assessment_with_alert_error),
                    5000);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            snackbar.show();
            assessmentAdapter.notifyDataSetChanged();
        }
    }
}
