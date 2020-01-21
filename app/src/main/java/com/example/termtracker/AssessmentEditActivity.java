package com.example.termtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.Assessment.*;
import com.example.termtracker.entity.AssessmentWithCourse;
import com.example.termtracker.entity.Course;
import com.example.termtracker.util.DateTimeUtilException;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.AssessmentEditViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssessmentEditActivity extends AppCompatActivity {
    AssessmentEditViewModel viewModel;
    Assessment editAssessment;
    List<Course> allCourses;
    Course selectedCourse;
    Assessment.AssessmentStatus selectedStatus;

    EditText titleEditText;
    EditText goalDateEditText;
    EditText descriptionEditText;
    RadioGroup typeRadioGroup;
    RadioButton objectiveRadioBtn;
    RadioButton performanceRadioBtn;
    Spinner courseSpinner;
    Spinner statusSpinner;
    ImageButton goalDatePickerImageBtn;
    Button cancelBtn;
    Button submitBtn;
    TextView courseDatesTextView;
    TextView titleErrorTextView;
    TextView courseErrorTextView;
    TextView goalDateErrorTextView;
    TextView statusErrorTextView;
    TextView descriptionErrorTextView;
    DatePickerDialog  goalDatePickerDialog;
    List<Course> courseSpinList;
    ArrayAdapter<Course> courseSpinAdapter;
    ArrayList<AssessmentStatus> statusSpinList;
    ArrayAdapter<AssessmentStatus> statusSpinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);

        titleEditText = findViewById(R.id.assessmentEditTitleEditText);
        goalDateEditText = findViewById(R.id.assessmentEditGoalDateEditText);
        descriptionEditText = findViewById(R.id.assessmentEditDescriptionEditText);
        typeRadioGroup = findViewById(R.id.assessmentEditTypeRadioGroup);
        objectiveRadioBtn = findViewById(R.id.assessmentEditObjectiveRadioBtn);
        performanceRadioBtn = findViewById(R.id.assessmentEditPerformanceRadioBtn);
        courseSpinner = findViewById(R.id.assessmentEditCourseSpinner);
        statusSpinner = findViewById(R.id.assessmentEditStatusSpinner);
        goalDatePickerImageBtn = findViewById(R.id.assessmentEditGoalDatePickerImageBtn);
        cancelBtn = findViewById(R.id.assessmentEditCancelBtn);
        submitBtn = findViewById(R.id.assessmentEditSubmitBtn);
        courseDatesTextView = findViewById(R.id.assessmentEditCourseDatesTextView);
        titleErrorTextView = findViewById(R.id.assessmentEditTitleErrorTextView);
        courseErrorTextView = findViewById(R.id.assessmentEditCourseErrorTextView);
        goalDateErrorTextView = findViewById(R.id.assessmentEditGoalDateErrorTextView);
        statusErrorTextView = findViewById(R.id.assessmentEditStatusErrorTextView);
        descriptionErrorTextView = findViewById(R.id.assessmentEditDescriptionErrorTextView);

        courseSpinList = new ArrayList<>();
        statusSpinList = new ArrayList<>();

        statusSpinList.add(AssessmentStatus.NOT_SCHEDULED);
        statusSpinList.add(AssessmentStatus.SCHEDULED);
        statusSpinList.add(AssessmentStatus.SUBMITTED);
        statusSpinList.add(AssessmentStatus.PASSED);
        statusSpinList.add(AssessmentStatus.FAILED);

        viewModel = ViewModelProviders.of(this).get(AssessmentEditViewModel.class);

        viewModel.getAllCourses().observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                allCourses = courses;
                courseSpinList.clear();
                courseSpinList.addAll(allCourses);
                courseSpinAdapter.notifyDataSetChanged();
            }
        });

        courseSpinAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        courseSpinList);
        courseSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseSpinAdapter);
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
                selectedCourse = (Course) spinner.getSelectedItem();
                String courseDateString = selectedCourse.getStartDate()
                        .format(DateTimeUtils.SHORT_DATE)
                        + "  -  "
                        + selectedCourse.getAnticipatedEndDate()
                        .format(DateTimeUtils.SHORT_DATE);
                courseDatesTextView
                        .setText(courseDateString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCourse = null;
                courseErrorTextView.setText(R.string.course_required);
            }
        });

        statusSpinAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                        statusSpinList);
        statusSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusSpinAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
                selectedStatus = (AssessmentStatus) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStatus = null;
                statusErrorTextView.setText(R.string.status_required);
            }
        });

        Intent intent = getIntent();
        final int activityMode = intent.getIntExtra(getString(R.string.edit_assessment_mode_key), 0);
        if (activityMode == 0) {
            AssessmentWithCourse receivedAssessment = intent.getParcelableExtra(getString(R.string.assessment_object_key));
            editAssessment = receivedAssessment.getAssessment();
            titleEditText.setText(editAssessment.getTitle());

            switch (editAssessment.getType()) {
                case OBJECTIVE:
                    objectiveRadioBtn.setChecked(true);
                    break;
                case PERFORMANCE:
                    performanceRadioBtn.setChecked(true);
                    break;
            }

            int selectedCourseIndex = courseSpinList.indexOf(receivedAssessment.getCourse());
            courseSpinner.setSelection(selectedCourseIndex);

            String courseDateString = receivedAssessment.getCourse().getStartDate()
                        .format(DateTimeUtils.SHORT_DATE)
                    + "  -  "
                    + receivedAssessment.getCourse().getAnticipatedEndDate()
                        .format(DateTimeUtils.SHORT_DATE);
            courseDatesTextView
                    .setText(courseDateString);

            goalDateEditText.setText(editAssessment.getGoalDate().format(DateTimeUtils.SHORT_DATE));

            int selectedStatusIndex = statusSpinList.indexOf(editAssessment.getStatus());
            statusSpinner.setSelection(selectedStatusIndex);

            descriptionEditText.setText(editAssessment.getDescription());
        } else {
            editAssessment = new Assessment();
            titleEditText.requestFocus();
        }

        goalDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing here
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateGoalDate();
            }
        });

        goalDatePickerImageBtn.setOnClickListener(v -> handleGoalDatePickerImageBtn());
        cancelBtn.setOnClickListener(v -> handleCancelBtn());
        submitBtn.setOnClickListener(v -> handleSubmitBtn(activityMode));
    }

    private void handleGoalDatePickerImageBtn() {
        if (selectedCourse != null) {
            goalDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    goalDateEditText.setText(LocalDate.of(year, month +1, dayOfMonth)
                            .format(DateTimeUtils.SHORT_DATE));
                }
            },selectedCourse.getAnticipatedEndDate().getYear(),
            selectedCourse.getAnticipatedEndDate().getMonthValue()-1,
            selectedCourse.getAnticipatedEndDate().getDayOfMonth());
        } else {
            goalDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    goalDateEditText.setText(LocalDate.of(year, month +1, dayOfMonth)
                            .format(DateTimeUtils.SHORT_DATE));
                }
            }, LocalDate.now().getYear(),
            LocalDate.now().getMonthValue()+1,
            LocalDate.now().getDayOfMonth());
        }
        goalDatePickerDialog.show();
    }

    private void handleCancelBtn() {
        this.setResult(RESULT_CANCELED);
        finish();
    }

    private void handleSubmitBtn(int activityMode) {
        boolean validated = validateAssessment(activityMode);
        if (validated) {
            editAssessment.setTitle(titleEditText.getText().toString());
            int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();

            if (selectedTypeId == objectiveRadioBtn.getId()) {
                editAssessment.setType(AssessmentType.OBJECTIVE);
            } else if (selectedTypeId == performanceRadioBtn.getId()) {
                editAssessment.setType(AssessmentType.PERFORMANCE);
            }

            editAssessment.setCourseId(selectedCourse.getId());
            editAssessment.setGoalDate(LocalDate.parse(goalDateEditText.getText(),
                    DateTimeUtils.SHORT_DATE));
            editAssessment.setStatus(selectedStatus);
            editAssessment.setDescription(descriptionEditText.getText().toString());

            if (activityMode == 0) {
                viewModel.updateAssessment(editAssessment);
            } else {
                viewModel.insertAssessment(editAssessment);
            }

            this.setResult(RESULT_OK);
            finish();
        }
    }

    private boolean validateAssessment(int activityMode) {
        boolean titleValid = validateTitle();
        boolean courseValid = validateCourse();
        boolean goalDateValid = validateGoalDate();
        boolean statusValid = validateStatus();
        boolean descriptionValid = validateDescription();

        return titleValid && courseValid && goalDateValid && statusValid && descriptionValid;
    }

    private boolean validateTitle() {
        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleErrorTextView.setText(R.string.assessment_name_required);
            return false;
        }
        else {
            titleErrorTextView.setText("");
            return true;
        }
    }

    private boolean validateCourse() {
        if (selectedCourse != null) {
            courseErrorTextView.setText("");
            return true;
        }
        else {
            courseErrorTextView.setText(R.string.course_required);
            return false;
        }
    }

    private boolean validateGoalDate() {
        String dateText = goalDateEditText.getText().toString();
        boolean goalDateValid = false;
        if (DateTimeUtils.dateStringIsValid(dateText)) {
            goalDateErrorTextView.setText("");
            goalDateValid = true;
        } else {
            goalDateErrorTextView.setText(getString(R.string.enter_valid_date));
            goalDateValid = false;
        }
        if (goalDateValid && (selectedCourse != null)) {
            LocalDate goalDate = LocalDate.parse(dateText,DateTimeUtils.SHORT_DATE);
            if (isDateWithinCourseDates(dateText)) {
                goalDateErrorTextView.setText("");
                goalDateValid = true;
            } else {
                goalDateErrorTextView.setText(R.string.goal_date_not_within_course_dates);
                goalDateValid = false;
            }
        }
        return goalDateValid;
    }

    private boolean validateStatus() {
        if (selectedStatus != null) {
            statusErrorTextView.setText("");
            return true;
        }
        else {
            statusErrorTextView.setText(R.string.status_required);
            return false;
        }
    }

    private boolean validateDescription() {
        if (descriptionEditText.getText().toString().trim().isEmpty()) {
            descriptionErrorTextView.setText(R.string.description_required);
            return false;
        }
        else {
            descriptionErrorTextView.setText("");
            return true;
        }
    }

    private boolean isDateWithinCourseDates(String dateText) {
        try {
            return DateTimeUtils.dateIsBetween(LocalDate.parse(dateText,DateTimeUtils.SHORT_DATE),
                    selectedCourse.getStartDate(), selectedCourse.getAnticipatedEndDate());
        } catch (DateTimeUtilException e) {
            e.printStackTrace();
            return false;
        }
    }
}
