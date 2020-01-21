package com.example.termtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.TermWithCourses;
import com.example.termtracker.ui.CourseListAdapter;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.TermDetailViewModel;
import com.example.termtracker.view_model.TermDetailViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class TermDetailActivity extends AppCompatActivity {
    private static final int EDIT_TERM_REQ_CODE = 19;
    private static final int ADD_COURSE_REQ_CODE = 91;
    ActionMenuView menuView;
    CourseListAdapter courseListAdapter;
    TermDetailViewModel viewModel;
    TextView termDetailTermTextView;
    TextView termDetailTermStartTextView;
    TextView termDetailTermEndTextView;
    TextView termDetailCourseCountTextView;
    FloatingActionButton editTermFab;
    FloatingActionButton addCourseFab;
    TermWithCourses selectedTerm;
    Course selectedCourse;
    int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        termDetailTermTextView = this.findViewById(R.id.termDetailTermTextView);
        termDetailTermStartTextView = this.findViewById(R.id.termDetailTermStartTextView);
        termDetailTermEndTextView = this.findViewById(R.id.termDetailTermEndTextView);
        termDetailCourseCountTextView = this.findViewById(R.id.termDetailCourseCountTextView);
        editTermFab = this.findViewById(R.id.courseDetailEditFab);
        addCourseFab = this.findViewById(R.id.termDetailAddCourseFab);

        Intent intent = getIntent();

        termId = intent.getIntExtra(getString(R.string.term_id_key), -1);

        TermDetailViewModelFactory factory =
                new TermDetailViewModelFactory(this.getApplication(), termId);
        viewModel = ViewModelProviders.of(this, factory).get(TermDetailViewModel.class);
        selectedTerm = viewModel.getTermWithCourses().getValue();

        Toolbar appBar = findViewById(R.id.termDetailTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        RecyclerView courseRV = findViewById(R.id.termDetailRecyclerView);

        courseListAdapter =
                new CourseListAdapter(this, new CourseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Course course) {
                Intent courseDetailIntent = new Intent(TermDetailActivity.this, CourseDetailActivity.class);
                courseDetailIntent.putExtra("COURSE_ID", course.getId());
                startActivity(courseDetailIntent);
            }
        });

        courseRV.setAdapter(courseListAdapter);
        courseRV.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getTermWithCourses().observe(this, new Observer<TermWithCourses>() {
            @Override
            public void onChanged(TermWithCourses termWithCourses) {
                courseListAdapter.setCourses(termWithCourses.getCourses());
                selectedTerm = termWithCourses;
                updateView();
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
                selectedCourse = courseListAdapter.getCourseAt(viewHolder.getAdapterPosition());
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
                                viewModel.deleteCourse(selectedCourse);
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                courseListAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(courseRV);

        addCourseFab.setOnClickListener(v -> {
            Intent editCourseIntent = new Intent(TermDetailActivity.this, CourseEditActivity.class);
            editCourseIntent.putExtra(getString(R.string.edit_course_mode_key), 1);
            startActivityForResult(editCourseIntent, ADD_COURSE_REQ_CODE);
        });

        editTermFab.setOnClickListener(view -> {
            Intent editTermIntent = new Intent(TermDetailActivity.this, TermEditActivity.class);
            editTermIntent.putExtra(getString(R.string.edit_term_mode_key), 0);
            editTermIntent.putExtra(getString(R.string.term_object_key), selectedTerm.getTerm());
            startActivityForResult(editTermIntent, EDIT_TERM_REQ_CODE);
        } );
    }

    private void updateView() {
        termDetailTermTextView
                .setText(selectedTerm.getTerm().getTitle());
        termDetailTermStartTextView
                .setText(selectedTerm.getTerm().getStartDate().format(DateTimeUtils.SHORT_DATE));
        termDetailTermEndTextView
                .setText(selectedTerm.getTerm().getEndDate().format(DateTimeUtils.SHORT_DATE));
        termDetailCourseCountTextView.setText(String.valueOf(selectedTerm.getCourses().size()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_TERM_REQ_CODE:
                handleEditTermActivityResult(resultCode, data);
                break;
            case ADD_COURSE_REQ_CODE:
                handleEditCourseActivityResult(resultCode, data);
                break;
        }
    }

    private void handleEditCourseActivityResult(int resultCode, Intent data) {
        switch ( resultCode) {
            case RESULT_CANCELED:
                Toast.makeText(this,"Course edit cancelled", Toast.LENGTH_LONG).show();
                break;
            case RESULT_OK:
                Toast.makeText(this,"Course was edited", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void handleEditTermActivityResult(int resultCode, Intent data) {
        switch ( resultCode) {
            case RESULT_CANCELED:
                Toast.makeText(this,"Term edit cancelled", Toast.LENGTH_LONG).show();
                break;
            case RESULT_OK:
                Toast.makeText(this,"Term was edited", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( TermDetailActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(TermDetailActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(TermDetailActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(TermDetailActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(TermDetailActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }
}
