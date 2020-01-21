package com.example.termtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.termtracker.entity.Mentor;
import com.example.termtracker.view_model.MentorEditViewModel;

    public class MentorEditActivity extends AppCompatActivity {
        private MentorEditViewModel viewModel;
        private Mentor editMentor;

        private EditText firstNameEditText;
        private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView firstNameErrorTextView;
    private TextView lastNameErrorTextView;
    private TextView emailErrorTextView;
    private TextView phoneErrorTextView;
    private Button cancelBtn;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_edit);

        Intent intent = getIntent();
        if(!intent.hasExtra(getString(R.string.edit_mentor_mode_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            final int activityMode = intent.getIntExtra(getString(R.string.edit_mentor_mode_key), 1);
            if (activityMode == 0 && intent.hasExtra(getString(R.string.mentor_object_key))) {
                editMentor = intent.getParcelableExtra(getString(R.string.mentor_object_key));
            } else {
                editMentor = new Mentor();
            }
            initView(activityMode);
            initListeners(activityMode);
        }
    }

    private void initView(int activityMode) {
        viewModel = ViewModelProviders.of(this).get(MentorEditViewModel.class);

        firstNameEditText = findViewById(R.id.mentorEditFirstNameEditText);
        lastNameEditText = findViewById(R.id.mentorEditLastNameEditText);
        emailEditText = findViewById(R.id.mentorEditEmailEditText);
        phoneEditText = findViewById(R.id.mentorEditPhoneEditText);
        firstNameErrorTextView = findViewById(R.id.mentorEditFirstNameErrorTextView);
        lastNameErrorTextView = findViewById(R.id.mentorEditLastNameErrorTextView);
        emailErrorTextView = findViewById(R.id.mentorEditEmailErrorTextView);
        phoneErrorTextView = findViewById(R.id.mentorEditPhoneErrorTextView);
        cancelBtn = findViewById(R.id.mentorEditCancelBtn);
        submitBtn = findViewById(R.id.mentorEditSubmitBtn);

        if (activityMode == 0) {
            firstNameEditText.setText(editMentor.getFirstName());
            lastNameEditText.setText(editMentor.getLastName());
            emailEditText.setText(editMentor.getEmail());
            phoneEditText.setText(editMentor.getPhoneNumber());
        }
    }

    private void initListeners(int activityMode) {
        cancelBtn.setOnClickListener(v -> handleCancelBtn());
        submitBtn.setOnClickListener(v -> handleSubmitBtn(activityMode));
    }

    private void handleCancelBtn() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    private void handleSubmitBtn(int activityMode) {
        boolean validated = validateMentor();

        if (validated) {
            editMentor.setFirstName(firstNameEditText.getText().toString().trim());
            editMentor.setLastName(lastNameEditText.getText().toString().trim());
            editMentor.setEmail(emailEditText.getText().toString().trim());
            editMentor.setPhoneNumber(phoneEditText.getText().toString().trim());

            if (activityMode == 0) {
                viewModel.updateMentor(editMentor);
            } else {
                viewModel.insertMentor(editMentor);
            }

            this.setResult(RESULT_OK);
            finish();
        }
    }

    private boolean validateMentor() {
        boolean firstNameValidated = validateFirstName();
        boolean lastNameValidated = validateLastName();
        boolean emailValidated = validateEmail();
        boolean phoneValidated = validatePhone();

        return firstNameValidated && lastNameValidated && emailValidated && phoneValidated;
    }

    private boolean validateFirstName() {
        if (firstNameEditText.getText().toString().trim().isEmpty()) {
            firstNameErrorTextView.setText(R.string.first_name_reuired);
            return false;
        } else {
            firstNameErrorTextView.setText("");
            return true;
        }
    }

    private boolean validateLastName() {
        if (lastNameEditText.getText().toString().trim().isEmpty()) {
            lastNameErrorTextView.setText(R.string.last_name_required);
            return false;
        } else {
            lastNameErrorTextView.setText("");
            return true;
        }
    }

    private boolean validateEmail() {
        String emailText = emailEditText.getText().toString().trim();
        if (emailText.isEmpty()) {
            emailErrorTextView.setText(R.string.email_required);
            return false;
        } else {
            boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
            if (isEmail) {
                emailErrorTextView.setText("");
                return true;
            } else {
                emailErrorTextView.setText(R.string.enter_valid_email);
                return false;
            }
        }
    }

    private boolean validatePhone() {
        String phoneText = phoneEditText.getText().toString().trim();
        if (phoneText.isEmpty()) {
            phoneErrorTextView.setText(R.string.phone_required);
            return false;
        } else {
            boolean isPhone = Patterns.PHONE.matcher(phoneText).matches();
            if (isPhone) {
                phoneErrorTextView.setText("");
                return true;
            } else {
                phoneErrorTextView.setText(R.string.enter_valid_phone);
                return false;
            }
        }
    }
}


//