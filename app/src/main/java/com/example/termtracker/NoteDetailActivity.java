package com.example.termtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termtracker.entity.NoteWithCourse;
import com.example.termtracker.view_model.NoteDetailViewModel;
import com.example.termtracker.view_model.NoteDetailViewModelFactory;

public class NoteDetailActivity extends AppCompatActivity {
    private static final int EDIT_NOTE_REQ_CODE = 8;
    private static final int SHARE_NOTE_REQ_CODE = 97;

    NoteDetailViewModel viewModel;
    ActionMenuView menuView;
    NoteWithCourse selectedNote;

    TextView subHeaderTextView;
    TextView courseTextView;
    TextView noteTextView;
    ImageButton editImageBtn;
    ImageButton deleteImageBtn;
    ImageButton emailImageBtn;
    ImageButton textImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Intent intent = getIntent();
        if (!intent.hasExtra(getString(R.string.note_id_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            final int noteId = intent.getIntExtra(getString(R.string.note_id_key), -1);
            initView(noteId);
            initHandlers();
        }
    }

    private void initView(int noteId) {
        subHeaderTextView = findViewById(R.id.noteDetailSubHeaderTextView);
        courseTextView = findViewById(R.id.noteDetailCourseTextView);
        noteTextView = findViewById(R.id.noteDetailNoteTextView);
        editImageBtn = findViewById(R.id.noteDetailEditImageBtn);
        deleteImageBtn = findViewById(R.id.noteDetailDeleteImageBtn);
        emailImageBtn = findViewById(R.id.noteDetailEmailImageBtn);
        textImageBtn = findViewById(R.id.noteDetailTextImageBtn);

        Toolbar appBar = findViewById(R.id.noteDetailTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);

        NoteDetailViewModelFactory factory = new NoteDetailViewModelFactory(this.getApplication(),noteId);
        viewModel = ViewModelProviders.of(this, factory).get(NoteDetailViewModel.class);
        viewModel.getNote().observe(this, new Observer<NoteWithCourse>() {
            @Override
            public void onChanged(NoteWithCourse noteWithCourse) {
                selectedNote = noteWithCourse;
                loadData();
            }
        });
    }

    private void initHandlers() {
        editImageBtn.setOnClickListener(v -> handleEditImageBtn());
        deleteImageBtn.setOnClickListener(v -> handleDeleteImageBtn());
        emailImageBtn.setOnClickListener(v -> handleEmailImageBtn());
        textImageBtn.setOnClickListener(v -> handleTextImageBtn());
    }

    private void handleEditImageBtn() {
        Intent noteEditIntent = new Intent(NoteDetailActivity.this, NoteEditActivity.class);
        noteEditIntent.putExtra(getString(R.string.edit_note_mode_key), 0);
        noteEditIntent.putExtra(getString(R.string.course_object_key), selectedNote.getCourse());
        noteEditIntent.putExtra(getString(R.string.note_object_key), selectedNote.getNote());
        startActivityForResult(noteEditIntent, EDIT_NOTE_REQ_CODE);
    }

    private void handleDeleteImageBtn() {
        viewModel.deleteNote(selectedNote.getNote());
        finish();
    }

    private void handleEmailImageBtn() {
        String emailSubject = "Note from course: " + selectedNote.getCourse().getTitle();
        String emailBody= selectedNote.getNote().getTitle() + "\n\n" + selectedNote.getNote().getNote();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent,"Choose email client"));
    }

    private void handleTextImageBtn() {
        String smsBody = selectedNote.getNote().getTitle() + "\n\n" + selectedNote.getNote().getNote();
        Intent smsIntent = new Intent(Intent.ACTION_SEND);
        smsIntent.setType("text/plain");
        smsIntent.putExtra(Intent.EXTRA_TEXT, smsBody);
        startActivity(Intent.createChooser(smsIntent, "Choose app"));
    }

    private void handleEditNoteResult(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "Note edit error", Toast.LENGTH_LONG).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Note edit cancelled", Toast.LENGTH_LONG).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "Note edit successful", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void handleShareNoteResult(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "Note share error", Toast.LENGTH_LONG).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Note share cancelled", Toast.LENGTH_LONG).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "Note share successful", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void loadData() {
        subHeaderTextView.setText(selectedNote.getNote().getTitle());
        courseTextView.setText(selectedNote.getCourse().getTitle());
        noteTextView.setText(selectedNote.getNote().getNote());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_NOTE_REQ_CODE:
                handleEditNoteResult(resultCode, data);
                break;
            case SHARE_NOTE_REQ_CODE:
                handleShareNoteResult(resultCode, data);
                break;
        }
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( NoteDetailActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(NoteDetailActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(NoteDetailActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(NoteDetailActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(NoteDetailActivity.this, AlertActivity.class);
                startActivity(alertIntent);
                finish();
                break;
        }
        return true;
    }
}