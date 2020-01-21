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
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseAlert;
import com.example.termtracker.entity.CourseAlertWithCourse;
import com.example.termtracker.util.AlertReceiver;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.CourseAlertEditViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CourseAlertEditActivity extends AppCompatActivity implements InsertResult {
    CourseAlertEditViewModel viewModel;
    CourseAlert editCourseAlert;
    Course selectedCourse;
    AlarmManager alarmManager;

    EditText dateEditText;
    EditText timeEditText;
    EditText messageEditText;
    TextView courseTextView;
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
        setContentView(R.layout.activity_course_alert_edit);

        Intent intent = getIntent();

        if (!intent.hasExtra(getString(R.string.course_object_key))) {
            this.setResult(RESULT_FIRST_USER);
            finish();
        } else {
            selectedCourse =
                    intent.getParcelableExtra(getString(R.string.course_object_key));
            final int activityMode =
                    intent.getIntExtra(getString(R.string.edit_course_alert_mode_key), 1);
            initViewItems();
            initViewModel();
            initCourseAlert(intent, activityMode, selectedCourse);
            initListeners(activityMode);
        }
    }

    private void initViewItems() {
        dateEditText = findViewById(R.id.courseAlertEditDateEditText);
        timeEditText = findViewById(R.id.courseAlertEditTimeEditText);
        messageEditText = findViewById(R.id.courseAlertEditMessageEditText);
        courseTextView = findViewById(R.id.courseAlertEditCourseTextView);
        dateErrorTextView = findViewById(R.id.courseAlertEditDateErrorTextView);
        timeErrorTextView = findViewById(R.id.courseAlertEditTimeErrorTextView);
        messageErrorTextView = findViewById(R.id.courseAlertEditMessageErrorTextView);
        datePickerImageBtn = findViewById(R.id.courseAlertEditDateImageBtn);
        timePickerImageBtn = findViewById(R.id.courseAlertEditTimeImageBtn);
        cancelBtn = findViewById(R.id.courseAlertEditCancelBtn);
        setAlertBtn = findViewById(R.id.courseAlertEditSetAlertBtn);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(CourseAlertEditViewModel.class);
    }

    private void initCourseAlert(Intent intent, final int activityMode, Course course) {

        if(activityMode == 0) {
            CourseAlertWithCourse passedCourseAlert = intent.getParcelableExtra(getString(R.string.course_alert_object_key));
            editCourseAlert = passedCourseAlert.getCourseAlert();
            dateEditText.setText(editCourseAlert.getAlertTime().toLocalDate()
                    .format(DateTimeUtils.SHORT_DATE));
            timeEditText.setText(editCourseAlert.getAlertTime().toLocalTime()
                    .format(DateTimeUtils.SIMPLE_TIME));
            messageEditText.setText(editCourseAlert.getMessage());
        } else {
            editCourseAlert = new CourseAlert();
            editCourseAlert.setCourseId(course.getId());
        }

        String courseString = course.getTitle()
                + "\n\tGoal Date: "
                + course.getAnticipatedEndDate().format(DateTimeUtils.SHORT_DATE);
        courseTextView.setText(courseString);
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
        LocalDate now;
        now = LocalDate.now();
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
            editCourseAlert.setAlertTime(LocalDateTime.of(date,time));
            editCourseAlert.setMessage(messageEditText.getText().toString().trim());

            if (activityMode == 0) {
                viewModel.updateCourseAlert(editCourseAlert);
                setAlarm(editCourseAlert.getId());
            } else {
                viewModel.insertCourseAlert(editCourseAlert, this);
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
            if(date.isAfter(selectedCourse.getAnticipatedEndDate())){
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
                .putExtra(getString(R.string.alert_title_key), getString(R.string.termtracker_course_alert));
        alertReceiverIntent
                .putExtra(getString(R.string.alert_message_key), editCourseAlert.getAlertMessage());
        alertReceiverIntent.putExtra(getString(R.string.alert_id_key), editCourseAlert.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                // Subtract key from zero to ensure course alerts and assessment alerts can use the same receiver
                0 - id,
                alertReceiverIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d("Timmy Time", "handleSetAlertBtn: " + (0 - id));
        ZonedDateTime zDateTime  = ZonedDateTime.of(editCourseAlert.getAlertTime(), ZoneId.systemDefault());
        long alertTime = zDateTime.toInstant().toEpochMilli();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime,pendingIntent);

        this.setResult(RESULT_OK);
        finish();
    }
}


