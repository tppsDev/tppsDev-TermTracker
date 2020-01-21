package com.example.termtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.termtracker.database.InsertResult;
import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.AssessmentAlert;
import com.example.termtracker.entity.AssessmentAlertWithAssessment;
import com.example.termtracker.util.AlertReceiver;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.AssessmentAlertEditViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.xml.transform.Result;

public class AssessmentAlertEditActivity extends AppCompatActivity implements InsertResult {
    AssessmentAlertEditViewModel viewModel;
    AssessmentAlert editAssessmentAlert;
    Assessment selectedAssessment;
    AlarmManager alarmManager;

    EditText dateEditText;
    EditText timeEditText;
    EditText messageEditText;
    TextView assessmentTextView;
    TextView dateErrorTextView;
    TextView timeErrorTextView;
    TextView messageErrorTextView;
    ImageButton datePickerImageBtn;
    ImageButton timePickerImageBtn;
    Button cancelBtn;
    Button setAlertBtn;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_alert_edit);

        Intent intent = getIntent();

        if (!intent.hasExtra(getString(R.string.assessment_object_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            selectedAssessment =
                    intent.getParcelableExtra(getString(R.string.assessment_object_key));
            final int activityMode =
                    intent.getIntExtra(getString(R.string.edit_assessment_alert_mode_key), 1);
            initViewItems();
            initViewModel();
            initAssessmentAlert(intent, activityMode, selectedAssessment);
            initListeners(activityMode);
        }
    }

    private void initViewItems() {
        dateEditText = findViewById(R.id.assessmentAlertEditDateEditText);
        timeEditText = findViewById(R.id.assessmentAlertEditTimeEditText);
        messageEditText = findViewById(R.id.assessmentAlertEditMessageEditText);
        assessmentTextView = findViewById(R.id.assessmentAlertEditAssessmentTextView);
        dateErrorTextView = findViewById(R.id.assessmentAlertEditDateErrorTextView);
        timeErrorTextView = findViewById(R.id.assessmentAlertEditTimeErrorTextView);
        messageErrorTextView = findViewById(R.id.assessmentAlertEditMessageErrorTextView);
        datePickerImageBtn = findViewById(R.id.assessmentAlertEditDateImageBtn);
        timePickerImageBtn = findViewById(R.id.assessmentAlertEditTimeImageBtn);
        cancelBtn = findViewById(R.id.assessmentAlertEditCancelBtn);
        setAlertBtn = findViewById(R.id.assessmentAlertEditSetAlertBtn);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(AssessmentAlertEditViewModel.class);
    }

    private void initAssessmentAlert(Intent intent, final int activityMode, Assessment assessment) {

        if(activityMode == 0) {
            AssessmentAlertWithAssessment passedAssessmentAlert = intent.getParcelableExtra(getString(R.string.assessment_alert_object_key));
            editAssessmentAlert = passedAssessmentAlert.getAssessmentAlert();
            dateEditText.setText(editAssessmentAlert.getAlertTime().toLocalDate()
                    .format(DateTimeUtils.SHORT_DATE));
            timeEditText.setText(editAssessmentAlert.getAlertTime().toLocalTime()
                    .format(DateTimeUtils.SIMPLE_TIME));
            messageEditText.setText(editAssessmentAlert.getMessage());
        } else {
            editAssessmentAlert = new AssessmentAlert();
            editAssessmentAlert.setAssessmentId(assessment.getId());
        }

        String assessmentString = assessment.getTitle()
                + "\n\tGoal Date: "
                + assessment.getGoalDate().format(DateTimeUtils.SHORT_DATE);
        assessmentTextView.setText(assessmentString);
    }

    private void initListeners(final int activityMode) {
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateDate();
            }
        });

        timeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateTime();
            }
        });

        datePickerImageBtn.setOnClickListener(v -> handleDatePickerImageBtn());
        timePickerImageBtn.setOnClickListener(v -> handleTimePickerImageBtn());
        cancelBtn.setOnClickListener(v -> handleCancelBtn());
        setAlertBtn.setOnClickListener(v -> handleSetAlertBtn(activityMode));
    }

    private void handleDatePickerImageBtn() {
        LocalDate now = LocalDate.now();
        datePickerDialog = new DatePickerDialog(this, datePickerListener, now.getYear(),
                now.getMonthValue() - 1,
                now.getDayOfMonth());
        datePickerDialog.show();
    }

    private void handleTimePickerImageBtn() {
        LocalTime time = LocalTime.of(8,0);
        timePickerDialog = new TimePickerDialog(this, timePickerListener,
                time.getHour(),
                time.getMinute(),
                false);
        timePickerDialog.show();
    }

    private void handleCancelBtn() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    private void handleSetAlertBtn(final int activityMode) {
        boolean validated = validateAlert(activityMode);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (validated && alarmManager != null) {
            LocalDate date = LocalDate.parse(dateEditText.getText(),DateTimeUtils.SHORT_DATE);
            LocalTime time = LocalTime.parse(timeEditText.getText(), DateTimeUtils.SIMPLE_TIME);
            editAssessmentAlert.setAlertTime(LocalDateTime.of(date,time));
            editAssessmentAlert.setMessage(messageEditText.getText().toString().trim());

            if (activityMode == 0) {
                viewModel.updateAssessmentAlert(editAssessmentAlert);
                setAlarm(editAssessmentAlert.getId());
            } else {
                viewModel.insertAssessmentAlert(editAssessmentAlert, this);
            }

        } else if (alarmManager == null) {
            Snackbar snackbar = Snackbar.make(setAlertBtn.getRootView(),
                    getString(R.string.no_alarm_mgr_error),
                    5000);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            snackbar.show();
        }
    }

    private boolean validateAlert(final int activityMode) {
        boolean dateValid = validateDate();
        boolean timeValid = validateTime();
        boolean messageValid = validateMessage();

        return dateValid && timeValid && messageValid;
    }

    private boolean validateDate() {
        String dateText = dateEditText.getText().toString();
        LocalDate date;
        if (DateTimeUtils.dateStringIsValid(dateText)) {
            date = LocalDate.parse(dateText, DateTimeUtils.SHORT_DATE);
            if(date.isAfter(selectedAssessment.getGoalDate())){
                dateErrorTextView.setText(R.string.alert_after_goal_date);
                return false;
            } else {
                dateErrorTextView.setText("");
                return true;
            }
        } else {
            dateErrorTextView.setText(R.string.enter_valid_date);
            return false;
        }
    }

    private boolean validateTime() {
        String timeText = timeEditText.getText().toString();
        if (DateTimeUtils.timeStringIsValid(timeText)) {
            timeErrorTextView.setText("");
            return true;
        } else {
            timeErrorTextView.setText(R.string.enter_valid_time);
            return false;
        }
    }

    private boolean validateMessage() {
        if (messageEditText.getText().toString().trim().isEmpty()) {
            messageErrorTextView.setText(R.string.message_required);
            return false;
        } else {
            messageErrorTextView.setText("");
            return true;
        }
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            dateEditText.setText(LocalDate.of(year, month +1, dayOfMonth)
                    .format(DateTimeUtils.SHORT_DATE));
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            timeEditText.setText(LocalTime.of(hourOfDay, minute)
                    .format(DateTimeUtils.SIMPLE_TIME));
        }
    };

    @Override
    public void handleInsertResult(long id) {
        setAlarm((int)id);
    }

    private void setAlarm(int id) {
        Intent alertReceiverIntent = new Intent(getApplicationContext(), AlertReceiver.class);
        alertReceiverIntent
                .putExtra(getString(R.string.alert_title_key), getString(R.string.termtracker_assessment_alert));
        alertReceiverIntent
                .putExtra(getString(R.string.alert_message_key), editAssessmentAlert.getAlertMessage());
        alertReceiverIntent.putExtra(getString(R.string.alert_id_key),  id);
        Log.d("Timmy Time", "setAlarm: " + id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                id,
                alertReceiverIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        ZonedDateTime zDateTime  = ZonedDateTime.of(editAssessmentAlert.getAlertTime(), ZoneId.systemDefault());
        long alertTime = zDateTime.toInstant().toEpochMilli();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime,pendingIntent);

        this.setResult(RESULT_OK);
        finish();
    }
}
