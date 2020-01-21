package com.example.termtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.ActionMenuView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.CourseWithAssessments;
import com.example.termtracker.entity.CourseWithMentors;
import com.example.termtracker.entity.CourseWithNotes;
import com.example.termtracker.entity.CourseWithTerm;
import com.example.termtracker.entity.Note;
import com.example.termtracker.ui.CourseDetailAssessmentListAdapter;
import com.example.termtracker.ui.NoteTitleListAdapter;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.CourseDetailViewModel;
import com.example.termtracker.view_model.CourseDetailViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class CourseDetailActivity extends AppCompatActivity implements ConstraintCheck {
    private static final int EDIT_COURSE_REQ_CODE = 3;
    private static final int ADD_ASSESSMENT_REQ_CODE = 1;
    private static final int ADD_NOTE_REQ_CODE = 11;
    private static final int ADD_ALERT_REQ_CODE = 13;

    ActionMenuView menuView;
    CourseDetailAssessmentListAdapter assessmentListAdapter;
    NoteTitleListAdapter noteListAdapter;
    CourseDetailViewModel viewModel;

    TextView courseTitleTextView;
    TextView courseStartDateTextView;
    TextView courseEndDateTextView;
    TextView courseAssessmentCountTextView;
    FloatingActionButton editCourseFab;
    FloatingActionButton addAssessmentFab;
    FloatingActionButton addNoteFab;
    ImageButton addAlertImgBtn;
    ImageButton viewMentorsImgBtn;

    CourseWithTerm selectedCourse;
    CourseWithMentors selectedCourseWithMentors;

    Assessment selectedAssessment;

    int courseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        courseTitleTextView = this.findViewById(R.id.courseDetailTitleTextView);
        courseStartDateTextView = this.findViewById(R.id.courseDetailStartDateTextView);
        courseEndDateTextView = this.findViewById(R.id.courseDetailEndDateTextView);
        courseAssessmentCountTextView = this.findViewById(R.id.courseDetailAssessmentCountTextView);
        editCourseFab = this.findViewById(R.id.courseDetailEditFab);
        addAssessmentFab = this.findViewById(R.id.courseDetailAddAssessmentFab);
        addNoteFab = this.findViewById(R.id.courseDetailAddNoteFab);
        addAlertImgBtn = this.findViewById(R.id.courseDetailAddAlertImageButton);
        viewMentorsImgBtn = this.findViewById(R.id.courseDetailViewMentorsImageButton);

        Intent intent = getIntent();
        courseId = intent.getIntExtra(getString(R.string.course_id_key), -1);

        CourseDetailViewModelFactory factory =
                new CourseDetailViewModelFactory(this.getApplication(), courseId);
        viewModel = ViewModelProviders.of(this,factory).get(CourseDetailViewModel.class);
        selectedCourse = viewModel.getCourse().getValue();

        Toolbar appBar = findViewById(R.id.courseDetailTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        RecyclerView assessmentRV = findViewById(R.id.courseDetailAssessmentRV);
        assessmentListAdapter = new
                CourseDetailAssessmentListAdapter(this, new CourseDetailAssessmentListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Assessment item) {
                handleAssessmentItemClick(item);
            }
        });
        assessmentRV.setAdapter(assessmentListAdapter);
        assessmentRV.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView noteRV = findViewById(R.id.courseDetailNoteRV);
        noteListAdapter = new NoteTitleListAdapter(this, new NoteTitleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note item) {
                handleNoteItemClick(item);
            }
        });
        noteRV.setAdapter(noteListAdapter);
        noteRV.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getCourse().observe(this, new Observer<CourseWithTerm>() {
            @Override
            public void onChanged(CourseWithTerm courseWithTerm) {
                selectedCourse = courseWithTerm;
                updateHeader();
            }
        });

        viewModel.getCourseWithAssessments().observe(this, new Observer<CourseWithAssessments>() {
            @Override
            public void onChanged(CourseWithAssessments courseWithAssessments) {
                assessmentListAdapter.setCourse(courseWithAssessments);
                courseAssessmentCountTextView
                        .setText(String.valueOf(courseWithAssessments.getAssessments().size()));
            }
        });

        viewModel.getCourseWithNotes().observe(this, new Observer<CourseWithNotes>() {
            @Override
            public void onChanged(CourseWithNotes courseWithNotes) {
                noteListAdapter.setNotes(courseWithNotes.getNotes());
            }
        });

        viewModel.getCourseWithMentors().observe(this, new Observer<CourseWithMentors>() {
            @Override
            public void onChanged(CourseWithMentors courseWithMentors) {
                selectedCourseWithMentors = courseWithMentors;
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
                selectedAssessment = assessmentListAdapter.getAssessmentAt(viewHolder.getAdapterPosition());
                int assessmentId = selectedAssessment.getId();

                viewModel.getAlertCountByAssessmentId(assessmentId, CourseDetailActivity.this);

            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(assessmentRV);

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
                Note note = noteListAdapter.getNoteAt(viewHolder.getAdapterPosition());

                viewModel.deleteNote(note);

            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(noteRV);

        editCourseFab.setOnClickListener(v -> handleEditCourseFab());
        addAssessmentFab.setOnClickListener(v -> handleAddAssessmentFab());
        addNoteFab.setOnClickListener(v -> handleAddNoteFab());
        addAlertImgBtn.setOnClickListener(v -> handleAddAlertImgBtn());
        viewMentorsImgBtn.setOnClickListener(v -> handleViewMentorsImgBtn());
    }

    private void handleAssessmentItemClick(Assessment assessment) {
        Intent assessmentDetailIntent =
                new Intent(CourseDetailActivity.this, AssessmentDetailActivity.class);
        assessmentDetailIntent.putExtra("ASSESSMENT_ID", assessment.getId());
        startActivity(assessmentDetailIntent);
    }

    private void handleNoteItemClick(Note note) {
        Intent noteDetailIntent = new Intent(CourseDetailActivity.this, NoteDetailActivity.class);
        noteDetailIntent.putExtra(getString(R.string.note_id_key), note.getId());
        startActivity(noteDetailIntent);
    }

    private void handleEditCourseFab() {
        Intent editCourseIntent =
                new Intent(CourseDetailActivity.this, CourseEditActivity.class);
        editCourseIntent.putExtra(getString(R.string.edit_course_mode_key), 0);
        editCourseIntent.putExtra(getString(R.string.course_object_key), selectedCourse);
        startActivityForResult(editCourseIntent, EDIT_COURSE_REQ_CODE);
    }

    private void handleAddAssessmentFab() {
        Intent editAssessmentIntent =
                new Intent(CourseDetailActivity.this, AssessmentEditActivity.class);
        editAssessmentIntent.putExtra(getString(R.string.edit_assessment_mode_key), 1);
        startActivityForResult(editAssessmentIntent, ADD_ASSESSMENT_REQ_CODE);
    }

    private void handleAddNoteFab() {
        Intent noteEditIntent = new Intent(CourseDetailActivity.this, NoteEditActivity.class);
        noteEditIntent.putExtra(getString(R.string.edit_note_mode_key), 1);
        noteEditIntent.putExtra(getString(R.string.course_object_key), selectedCourse.getCourse());
        startActivityForResult(noteEditIntent, ADD_NOTE_REQ_CODE);
    }

    private void handleAddAlertImgBtn() {
        Intent editCourseAlertIntent =
                new Intent(CourseDetailActivity.this, CourseAlertEditActivity.class);
        editCourseAlertIntent
                .putExtra(getString(R.string.course_object_key), selectedCourse.getCourse());
        editCourseAlertIntent.putExtra(getString(R.string.edit_course_alert_mode_key), 1);
        startActivityForResult(editCourseAlertIntent, ADD_ALERT_REQ_CODE);

    }

    private void handleViewMentorsImgBtn() {
        Intent courseMentorsIntent = new Intent(CourseDetailActivity.this, CourseMentorsActivity.class);
        courseMentorsIntent.putExtra(getString(R.string.course_with_mentors_object_key), selectedCourseWithMentors);
        startActivity(courseMentorsIntent);
    }

    private void updateHeader() {
        courseTitleTextView.setText(selectedCourse.getCourse().getTitle());
        courseStartDateTextView
                .setText(selectedCourse.getCourse().getStartDate()
                        .format(DateTimeUtils.SHORT_DATE));
        courseEndDateTextView
                .setText(selectedCourse.getCourse().getAnticipatedEndDate()
                        .format(DateTimeUtils.SHORT_DATE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_COURSE_REQ_CODE:
                handleEditCourseResult(resultCode, data);
                break;
            case ADD_ASSESSMENT_REQ_CODE:
                handleAddAssessmentResult(resultCode, data);
                break;
            case ADD_ALERT_REQ_CODE:
                handleAddAlertResult(resultCode, data);
                break;
            case ADD_NOTE_REQ_CODE:
                handleAddNoteResult(resultCode, data);
                break;
        }
    }

    private void handleEditCourseResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Course edited", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Edit course cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddAssessmentResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Add assessment cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddAlertResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Alert added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Add alert cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddNoteResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Add note cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( CourseDetailActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(CourseDetailActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent( CourseDetailActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(CourseDetailActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(CourseDetailActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void handleConstraintCheck(int count) {
        if (count < 1) {
            viewModel.deleteAssessment(selectedAssessment);
        } else {
            Snackbar snackbar = Snackbar.make(addAlertImgBtn,
                    getString(R.string.delete_assessment_with_alert_error),
                    5000);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            snackbar.show();
            assessmentListAdapter.notifyDataSetChanged();
        }
    }
}
