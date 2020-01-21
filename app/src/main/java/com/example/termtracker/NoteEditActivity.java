package com.example.termtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.Note;
import com.example.termtracker.entity.NoteWithCourse;
import com.example.termtracker.view_model.NoteEditViewModel;

public class NoteEditActivity extends AppCompatActivity {
    private NoteEditViewModel viewModel;
    private Note editNote;
    private Course selectedCourse;

    private TextView subHeaderTextView;
    private EditText titleEditText;
    private EditText noteEditText;
    private TextView titleErrorTextView;
    private TextView noteErrorTextView;
    private Button cancelBtn;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        viewModel = ViewModelProviders.of(this).get(NoteEditViewModel.class);

        Intent intent = getIntent();
        if (!intent.hasExtra(getString(R.string.course_object_key))
            || !intent.hasExtra(getString(R.string.edit_note_mode_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            selectedCourse = intent.getParcelableExtra(getString(R.string.course_object_key));
            final int activityMode = intent.getIntExtra(getString(R.string.edit_note_mode_key), 1);
            initViewItems();
            initEditNote(intent, activityMode, selectedCourse);
            initListeners(activityMode);
        }
    }

    private void initViewItems() {
        subHeaderTextView = findViewById(R.id.noteEditSubHeaderTextView);
        titleEditText = findViewById(R.id.noteEditTitleEditText);
        noteEditText = findViewById(R.id.noteEditNoteEditText);
        titleErrorTextView = findViewById(R.id.noteEditTitleErrorTextView);
        noteErrorTextView = findViewById(R.id.noteEditNoteErrorTextView);
        cancelBtn = findViewById(R.id.noteEditCancelBtn);
        submitBtn = findViewById(R.id.noteEditSubmitBtn);

        String subHeaderStr = selectedCourse.getTitle() + " Note";
        subHeaderTextView.setText(subHeaderStr);
    }

    private void initEditNote(Intent intent, int activityMode, Course selectedCourse) {
        if (activityMode == 0) {
            editNote = intent.getParcelableExtra(getString(R.string.note_object_key));
            titleEditText.setText(editNote.getTitle());
            noteEditText.setText(editNote.getNote());
        } else {
            editNote = new Note();
            editNote.setCourseId(selectedCourse.getId());
        }
    }

    private void initListeners(int activityMode) {
        cancelBtn.setOnClickListener(v -> {
            this.setResult(RESULT_CANCELED);
            finish();
        });

        submitBtn.setOnClickListener(v -> handleSubmitBtn(activityMode));
    }

    private void handleSubmitBtn(int activityMode) {
        boolean validated = validateNote(activityMode);
        if (validated) {
            editNote.setTitle(titleEditText.getText().toString().trim());
            editNote.setNote(noteEditText.getText().toString().trim());

            if (activityMode == 0) {
                viewModel.updateNote(editNote);
            } else {
                viewModel.insertNote(editNote);
            }

            this.setResult(RESULT_OK);
            finish();
        }
    }

    private boolean validateNote(int activityMode) {
        boolean titleValid = validateTitle();
        boolean noteValid = validateNoteText();

        return titleValid && noteValid;
    }

    private boolean validateTitle() {
        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleErrorTextView.setText(R.string.note_title_required);
            return false;
        } else {
            titleErrorTextView.setText("");
            return true;
        }
    }

    private boolean validateNoteText() {
        if (noteEditText.getText().toString().trim().isEmpty()) {
            noteErrorTextView.setText(R.string.note_body_required);
            return false;
        } else {
            noteErrorTextView.setText("");
            return true;
        }
    }
}
