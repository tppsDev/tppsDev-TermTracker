package com.example.termtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseWithTerm;
import com.example.termtracker.ui.CourseWithTermListAdapter;
import com.example.termtracker.view_model.AllCoursesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CourseActivity extends AppCompatActivity {
    private static final int EDIT_COURSE_REQ_CODE = 80;
    ActionMenuView menuView;
    TextView userTextView;
    FloatingActionButton addCourseFab;
    CourseWithTermListAdapter courseAdapter;
    AllCoursesViewModel coursesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        coursesViewModel = ViewModelProviders.of(this).get(AllCoursesViewModel.class);

        Toolbar appBar = findViewById(R.id.courseTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        SharedPreferences sPref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userTextView = findViewById(R.id.activityCourseUserTextView);
        String user = sPref.getString(getString(R.string.user_name_key), "User");
        user = user + "'s Courses";
        userTextView.setText(user);

        RecyclerView courseRecyclerView = findViewById(R.id.contentCourseRecyclerView);

        courseAdapter =
                new CourseWithTermListAdapter(this,
                        new CourseWithTermListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CourseWithTerm course) {
                Intent courseDetailIntent =
                        new Intent(CourseActivity.this, CourseDetailActivity.class);
                courseDetailIntent.putExtra("COURSE_ID", course.getCourse().getId());
                startActivity(courseDetailIntent);
            }
        });

        courseRecyclerView.setAdapter(courseAdapter);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        coursesViewModel
                .getAllCoursesWithTerms()
                .observe(this, new Observer<List<CourseWithTerm>>() {
            @Override
            public void onChanged(List<CourseWithTerm> courseWithTerms) {
                courseAdapter.setCourses(courseWithTerms);
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
                Course selectedCourse = courseAdapter.getCourseAt(viewHolder.getAdapterPosition());
                new AlertDialog.Builder(addCourseFab.getContext())
                        .setTitle("Confirm Delete")
                        .setMessage("Deleting course will delete all associated assessments and notes"
                                + "\n\n"
                                + "Are you sure you want to delete: "
                                + selectedCourse.getTitle()
                                + "?")
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                coursesViewModel.deleteCourse(selectedCourse);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                courseAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(courseRecyclerView);

        addCourseFab = findViewById(R.id.courseContentAddButton);
        addCourseFab.setOnClickListener(v -> handleAddCourseFab());
    }

    private void handleAddCourseFab() {
        Intent editCourseIntent = new Intent(CourseActivity.this, CourseEditActivity.class);
        editCourseIntent.putExtra(getString(R.string.edit_course_mode_key), 1);
        startActivityForResult(editCourseIntent, EDIT_COURSE_REQ_CODE);
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( CourseActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(CourseActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(CourseActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(CourseActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_COURSE_REQ_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Course Added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Add Course cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
