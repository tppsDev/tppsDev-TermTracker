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
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.AssessmentWithNotes;
import com.example.termtracker.entity.Note;
import com.example.termtracker.ui.NoteTitleListAdapter;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.AssessmentDetailViewModel;
import com.example.termtracker.view_model.AssessmentDetailViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AssessmentDetailActivity extends AppCompatActivity {
    private static final int EDIT_ASSESSMENT_REQ_CODE = 27;
    private static final int EDIT_ASSESSMENT_ALERT_REQ_CODE = 10;
    private static final int ADD_NOTE_REQ_CODE = 45;

    ActionMenuView menuView;
    NoteTitleListAdapter noteListAdapter;
    AssessmentDetailViewModel viewModel;

    TextView assessmentTitleTextView;
    TextView courseTitleText;
    TextView assessmentStatusTextView;
    TextView assessmentGoalDateTextView;
    TextView courseEndDateTextView;
    TextView descriptionTextView;
    ImageView assessmentTypeImageView;
    FloatingActionButton editAssessmentFab;
    FloatingActionButton addAlertFab;
    FloatingActionButton addNoteFab;

    AssessmentWithCourse selectedAssessmentWithCourse;

    int assessmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        assessmentTitleTextView = findViewById(R.id.assessmentDetailTitleTextView);
        courseTitleText = findViewById(R.id.assessmentDetailCourseTextView);
        assessmentStatusTextView = findViewById(R.id.assessmentDetailStatusTextView);
        assessmentGoalDateTextView = findViewById(R.id.assessmentDetailGoalDateTextView);
        courseEndDateTextView = findViewById(R.id.assessmentDetailCourseEndDateTextView);
        descriptionTextView = findViewById(R.id.assessmentDetailDescriptionTextView);
        assessmentTypeImageView = findViewById(R.id.assessmentDetailTypeImageView);
        editAssessmentFab = findViewById(R.id.assessmentDetailEditFab);
        addAlertFab = findViewById(R.id.assessmentDetailAddAlertFab);
        addNoteFab = findViewById(R.id.assessmentDetailAddNoteFab);

        Intent intent = getIntent();
        assessmentId = intent.getIntExtra(getString(R.string.assessment_id_key), -1);

        AssessmentDetailViewModelFactory factory =
                new AssessmentDetailViewModelFactory(this.getApplication(), assessmentId);
        viewModel = ViewModelProviders.of(this,factory).get(AssessmentDetailViewModel.class);

        Toolbar appBar = findViewById(R.id.assessmentDetailTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        RecyclerView noteRV = findViewById(R.id.assessmentDetailNoteRV);
        noteListAdapter = new NoteTitleListAdapter(this, new NoteTitleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note item) {
                handleNoteItemClick(item);
            }
        });
        noteRV.setAdapter(noteListAdapter);
        noteRV.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getAssessmentWithCourse().observe(this, new Observer<AssessmentWithCourse>() {
            @Override
            public void onChanged(AssessmentWithCourse assessmentWithCourse) {
                selectedAssessmentWithCourse = assessmentWithCourse;
                updateHeader();
            }
        });

        viewModel.getAssessmentWithNotes().observe(this, new Observer<AssessmentWithNotes>() {
            @Override
            public void onChanged(AssessmentWithNotes assessmentWithNotes) {
                noteListAdapter.setNotes(assessmentWithNotes.getNotes());
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
                Note selectedNote = noteListAdapter.getNoteAt(viewHolder.getAdapterPosition());

                viewModel.deleteNote(selectedNote);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return .95f;
            }
        }).attachToRecyclerView(noteRV);

        editAssessmentFab.setOnClickListener(v -> handleEditAssessmentFab());
        addAlertFab.setOnClickListener(v -> handleAddAlertFab());
        addNoteFab.setOnClickListener(v -> handleAddNoteFab());
    }

    private void updateHeader() {
        assessmentTitleTextView.setText(selectedAssessmentWithCourse.getAssessment().getTitle());
        courseTitleText.setText(selectedAssessmentWithCourse.getCourse().getTitle());
        assessmentStatusTextView
                .setText(selectedAssessmentWithCourse.getAssessment().getStatus().toString());
        assessmentGoalDateTextView
                .setText(selectedAssessmentWithCourse.getAssessment().getGoalDate()
                        .format(DateTimeUtils.SHORT_DATE));
        courseEndDateTextView
                .setText(selectedAssessmentWithCourse.getCourse().getAnticipatedEndDate()
                        .format(DateTimeUtils.SHORT_DATE));
        descriptionTextView.setText(selectedAssessmentWithCourse.getAssessment().getDescription());
        switch (selectedAssessmentWithCourse.getAssessment().getType()) {
            case OBJECTIVE:
                assessmentTypeImageView.setImageResource(R.drawable.ic_objective_assessment);
                break;
            case PERFORMANCE:
                assessmentTypeImageView.setImageResource(R.drawable.ic_performance_assessment);
                break;
        }

    }

    private void handleEditAssessmentFab() {
        Intent editAssessmentIntent =
                new Intent(AssessmentDetailActivity.this, AssessmentEditActivity.class);
        editAssessmentIntent.putExtra(getString(R.string.edit_course_mode_key), 0);
        editAssessmentIntent.putExtra(getString(R.string.assessment_object_key), selectedAssessmentWithCourse);
        startActivityForResult(editAssessmentIntent, EDIT_ASSESSMENT_REQ_CODE);
    }

    private void handleAddAlertFab() {
        Intent editAssessmentAlertIntent =
                new Intent(AssessmentDetailActivity.this, AssessmentAlertEditActivity.class);
        editAssessmentAlertIntent
                .putExtra(getString(R.string.assessment_object_key), selectedAssessmentWithCourse.getAssessment());
        editAssessmentAlertIntent.putExtra(getString(R.string.edit_assessment_alert_mode_key), 1);
        startActivityForResult(editAssessmentAlertIntent, EDIT_ASSESSMENT_ALERT_REQ_CODE);
    }

    private void handleAddNoteFab() {
        Intent noteEditIntent = new Intent(AssessmentDetailActivity.this, NoteEditActivity.class);
        noteEditIntent.putExtra(getString(R.string.edit_note_mode_key), 1);
        noteEditIntent.putExtra(getString(R.string.course_object_key), selectedAssessmentWithCourse.getCourse());
        startActivityForResult(noteEditIntent, ADD_NOTE_REQ_CODE);
    }

    private void handleNoteItemClick(Note note) {
        Intent noteDetailIntent = new Intent(AssessmentDetailActivity.this, NoteDetailActivity.class);
        noteDetailIntent.putExtra(getString(R.string.note_id_key), note.getId());
        startActivity(noteDetailIntent);
    }

    private void handleEditAssessmentResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Assessment edited", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Edit cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private void handleEditAssessmentAlertResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Alert added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Add alert cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAddNoteResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Note added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Add Note cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( AssessmentDetailActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(AssessmentDetailActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent( AssessmentDetailActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(AssessmentDetailActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(AssessmentDetailActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_ASSESSMENT_REQ_CODE:
                handleEditAssessmentResult( resultCode, data);
                break;
            case EDIT_ASSESSMENT_ALERT_REQ_CODE:
                handleEditAssessmentAlertResult(resultCode, data);
                break;
            case ADD_NOTE_REQ_CODE:
                handleAddNoteResult(resultCode, data);
                break;
        }
    }

}
