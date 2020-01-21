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

import com.example.termtracker.database.ConstraintCheck;
import com.example.termtracker.entity.Mentor;
import com.example.termtracker.view_model.MentorDetailViewModel;
import com.example.termtracker.view_model.MentorDetailViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

public class MentorDetailActivity extends AppCompatActivity implements ConstraintCheck {
    private static final int EDIT_MENTOR_REQ_CODE = 3;
    private static final int EMAIL_MENTOR_REQ_CODE = 47;
    private static final int TEXT_MENTOR_REQ_CODE = 11;
    private static final int CALL_MENTOR_REQ_CODE = 13;

    ActionMenuView menuView;
    MentorDetailViewModel viewModel;
    Mentor selectedMentor;

    TextView subHeaderTextView;
    TextView emailTextView;
    TextView phoneTextView;

    ImageButton editMentorImageBtn;
    ImageButton deleteMentorImageBtn;
    ImageButton sendEmailImageBtn;
    ImageButton sendSmsTextImageBtn;
    ImageButton callImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_detail);
        Intent intent = getIntent();

        if (!intent.hasExtra(getString(R.string.mentor_id_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            final int mentorId = intent.getIntExtra(getString(R.string.mentor_id_key), -1);
            initView(mentorId);
            initListeners();
        }
    }

    private void initView(final int mentorId) {
        subHeaderTextView = findViewById(R.id.mentorDetailSubHeaderTextView);
        emailTextView = findViewById(R.id.mentorDetailEmailTextView);
        phoneTextView = findViewById(R.id.mentorDetailPhoneTextView);

        editMentorImageBtn = findViewById(R.id.mentorDetailEditImageBtn);
        deleteMentorImageBtn = findViewById(R.id.mentorDetailDeleteImageBtn);
        sendEmailImageBtn = findViewById(R.id.mentorDetailEmailImageBtn);
        sendSmsTextImageBtn = findViewById(R.id.mentorDetailSmsTextImageBtn);
        callImageBtn = findViewById(R.id.mentorDetailPhoneImageBtn);

        MentorDetailViewModelFactory factory = new MentorDetailViewModelFactory(this.getApplication(), mentorId);
        viewModel = ViewModelProviders.of(this, factory).get(MentorDetailViewModel.class);

        Toolbar appBar = findViewById(R.id.mentorDetailTermTrackerAppBar);
        menuView = appBar.findViewById(R.id.appBarMenuView);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menuView.getMenu());
        setSupportActionBar(appBar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        viewModel.getMentor().observe(this, new Observer<Mentor>() {
            @Override
            public void onChanged(Mentor mentor) {
                selectedMentor = mentor;
                loadData();
            }
        });
    }

    private void loadData() {
        subHeaderTextView.setText(selectedMentor.getDisplayName());
        emailTextView.setText(selectedMentor.getEmail());
        phoneTextView.setText(selectedMentor.getPhoneNumber());
    }

    private void initListeners() {
        menuView.setOnMenuItemClickListener(this::menuItemSelectListener);
        editMentorImageBtn.setOnClickListener(v -> handleEditMentorImageBtn());
        deleteMentorImageBtn.setOnClickListener(v -> handleDeleteImageBtn());
        sendEmailImageBtn.setOnClickListener(v -> handleSendEmailImageBtn());
        sendSmsTextImageBtn.setOnClickListener(v -> handleSendSmsTextImageBtn());
        callImageBtn.setOnClickListener(v -> handleCallImageBtn());
    }

    private void handleEditMentorImageBtn() {
        Intent mentorEditIntent = new Intent(MentorDetailActivity.this, MentorEditActivity.class);
        mentorEditIntent.putExtra(getString(R.string.edit_mentor_mode_key), 0);
        mentorEditIntent.putExtra(getString(R.string.mentor_object_key), selectedMentor);
        startActivityForResult(mentorEditIntent, EDIT_MENTOR_REQ_CODE);
    }

    private void handleDeleteImageBtn() {
        int mentorId = selectedMentor.getId();
        viewModel.getCourseCountByMentorId(mentorId, this);
    }

    private void handleSendEmailImageBtn() {
        String[] addresses = {selectedMentor.getEmail()};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose email client"));
    }

    private void handleSendSmsTextImageBtn() {
        Uri uri = Uri.parse("smsto:" + selectedMentor.getPhoneNumber());
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(smsIntent);
    }

    private void handleCallImageBtn() {
        Uri uri = Uri.parse("tel:"+selectedMentor.getPhoneNumber().trim());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(callIntent);
    }

    private boolean menuItemSelectListener(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeItem:
                Intent homeIntent = new Intent( MentorDetailActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.termItem:
                Intent termIntent = new Intent(MentorDetailActivity.this, TermActivity.class);
                startActivity(termIntent);
                finish();
                break;
            case R.id.courseItem:
                Intent courseIntent = new Intent(MentorDetailActivity.this, CourseActivity.class);
                startActivity(courseIntent);
                finish();
                break;
            case R.id.assessmentItem:
                Intent assessmentIntent = new Intent(MentorDetailActivity.this, AssessmentActivity.class);
                startActivity(assessmentIntent);
                finish();
                break;
            case R.id.alertItem:
                Intent alertIntent = new Intent(MentorDetailActivity.this, AlertActivity.class);
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
            case EDIT_MENTOR_REQ_CODE:
                handleEditMentorResult(resultCode);
                break;
            case EMAIL_MENTOR_REQ_CODE:
                handleEmailMentorResult(resultCode);
                break;
            case TEXT_MENTOR_REQ_CODE:
                handleTextMentorResult(resultCode);
                break;
            case CALL_MENTOR_REQ_CODE:
                handleCallMentorResult(resultCode);
                break;
        }
    }

    private void handleEditMentorResult(int resultCode) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "Edit error", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Edit cancelled", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "Edit successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleEmailMentorResult(int resultCode) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "Email error", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Email cancelled", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "Email successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleTextMentorResult(int resultCode) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "SMS Text error", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "SMS Text cancelled", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "SMS Text successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCallMentorResult(int resultCode) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                Toast.makeText(this, "Call error", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Call cancelled", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_OK:
                Toast.makeText(this, "Call successful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void handleConstraintCheck(int count) {
        if (count < 1) {
            viewModel.deleteMentor(selectedMentor);
            finish();
        } else {
            Snackbar snackbar = Snackbar.make(menuView,
                    getString(R.string.delete_mentor_with_course_error),
                    5000);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            snackbar.show();
        }
    }
}
