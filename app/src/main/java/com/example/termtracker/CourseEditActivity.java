package com.example.termtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.termtracker.entity.Assessment;
import com.example.termtracker.entity.Course;
import com.example.termtracker.entity.CourseWithTerm;
import com.example.termtracker.entity.Term;
import com.example.termtracker.util.DateTimeUtilException;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.CourseEditViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseEditActivity extends AppCompatActivity {
    CourseEditViewModel viewModel;
    CourseWithTerm course;
    Course editCourse;
    List<Course> allCourses;
    List<Term> allTerms;
    Term selectedTerm;
    Course.CourseStatus selectedStatus;
    EditText titleEditText;
    EditText startDateEditText;
    EditText endDateEditText;
    TextView titleErrorTextView;
    TextView termErrorTextView;
    TextView startDateErrorTextView;
    TextView endDateErrorTextView;
    TextView statusErrorTextView;
    Spinner termSpinner;
    Spinner statusSpinner;
    ImageButton startDatePickerBtn;
    ImageButton endDatePickerBtn;
    Button cancelBtn;
    Button submitBtn;
    DatePickerDialog startDatePickerDialog;
    DatePickerDialog endDatePickerDialog;
    List<Term> termSpinList;
    ArrayAdapter<Term> spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);

        titleEditText = findViewById(R.id.editCourseTitleEditText);
        startDateEditText = findViewById(R.id.editCourseStartDateEditText);
        endDateEditText = findViewById(R.id.editCourseEndDateEditText);
        titleErrorTextView = findViewById(R.id.editCourseTitleErrorTextView);
        termErrorTextView = findViewById(R.id.editCourseTermErrorTextView);
        startDateErrorTextView = findViewById(R.id.editCourseStartDateErrorTextView);
        endDateErrorTextView = findViewById(R.id.editCourseEndDateErrorTextView);
        statusErrorTextView = findViewById(R.id.editCourseStatusErrorTextView);
        termSpinner = findViewById(R.id.editCourseTermSpinner);
        statusSpinner = findViewById(R.id.editCourseStatusSpinner);
        startDatePickerBtn = findViewById(R.id.editCourseStartDatePickerImageButton);
        endDatePickerBtn = findViewById(R.id.editCourseEndDatePickerImageButton);
        cancelBtn = findViewById(R.id.editCourseCancelBtn);
        submitBtn = findViewById(R.id.editCourseSubmitBtn);

        viewModel = ViewModelProviders.of(this).get(CourseEditViewModel.class);
        viewModel.getAllCourses().observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                allCourses = courses;
            }
        });

        viewModel.getAllTerms().observe(this, new Observer<List<Term>>() {
            @Override
            public void onChanged(List<Term> terms) {
                allTerms = terms;
                termSpinList.clear();
                termSpinList.addAll(allTerms);
                spinAdapter.notifyDataSetChanged();
            }
        });

        termSpinList = new ArrayList<>();

        spinAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, termSpinList);

        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        termSpinner.setAdapter(spinAdapter);

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
                selectedTerm = (Term) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
                selectedStatus = Course.CourseStatus.fromString(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        final int activityMode = intent.getIntExtra(getString(R.string.edit_course_mode_key), 0);
        if (activityMode == 0) {
            course = intent.getParcelableExtra(getString(R.string.course_object_key));
            editCourse = course.getCourse();
            titleEditText.setText(course.getCourse().getTitle());
            termSpinner.setSelection(((ArrayAdapter<Term>) termSpinner.getAdapter()).getPosition(course.getTerm()));
            int statusPosition = getStatusPosition();
            statusSpinner.setSelection(statusPosition);
            selectedTerm = course.getTerm();
            startDateEditText.setText(course.getCourse().getStartDate().format(DateTimeUtils.SHORT_DATE));
            endDateEditText.setText(course.getCourse().getAnticipatedEndDate().format(DateTimeUtils.SHORT_DATE));
            titleEditText.requestFocus();
        } else {
            editCourse = new Course();
            titleEditText.requestFocus();
        }

        startDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing here
            }

            @Override
            public void afterTextChanged(Editable s) {
                String dateText = startDateEditText.getText().toString();
                boolean startDatePassed = false;
                if (DateTimeUtils.dateStringIsValid(dateText)) {
                    startDateErrorTextView.setText("");
                    startDatePassed = true;
                } else {
                    startDateErrorTextView.setText(R.string.enter_valid_date);
                }
                String endDateText = endDateErrorTextView.getText().toString();
                if (startDatePassed && DateTimeUtils.dateStringIsValid(endDateText)) {
                    if (LocalDate.parse(dateText,DateTimeUtils.SHORT_DATE)
                            .isBefore(LocalDate.parse(endDateText))) {
                        startDateErrorTextView.setText("");
                    } else {
                        startDateErrorTextView.setText(R.string.start_date_after_end_date);
                    }
                }
            }
        });

        endDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing here
            }

            @Override
            public void afterTextChanged(Editable s) {
                String dateText = endDateEditText.getText().toString();
                boolean endDatePassed = false;
                if (DateTimeUtils.dateStringIsValid(dateText)) {
                    endDateErrorTextView.setText("");
                    endDatePassed = true;
                } else {
                    endDateErrorTextView.setText(R.string.enter_valid_date);
                }
                String startDateText = startDateEditText.getText().toString();
                if (endDatePassed && DateTimeUtils.dateStringIsValid(startDateText)) {
                    if (LocalDate.parse(dateText,DateTimeUtils.SHORT_DATE)
                            .isAfter(LocalDate.parse(startDateText,DateTimeUtils.SHORT_DATE))) {
                        endDateErrorTextView.setText("");
                    } else {
                        endDateErrorTextView.setText(R.string.end_date_before_start_date);
                    }
                }
            }
        });

        startDatePickerBtn.setOnClickListener(v -> {
            if (activityMode == 0) {
                startDatePickerDialog = new DatePickerDialog(this, onStartDateSetListener,
                        course.getCourse().getStartDate().getYear(),
                        course.getCourse().getStartDate().getMonthValue() -1,
                        course.getCourse().getStartDate().getDayOfMonth());
            } else if (selectedTerm != null) {
                startDatePickerDialog = new DatePickerDialog(this, onStartDateSetListener,
                        selectedTerm.getStartDate().getYear(),
                        selectedTerm.getStartDate().getMonthValue() -1 ,
                        selectedTerm.getStartDate().getDayOfMonth());
            } else {
                startDatePickerDialog = new DatePickerDialog(this, onStartDateSetListener,
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue() -1,
                        LocalDate.now().getDayOfMonth());
            }
            startDatePickerDialog.show();
        });

        endDatePickerBtn.setOnClickListener(v -> {
            if (activityMode == 0) {
                endDatePickerDialog = new DatePickerDialog(this, onEndDateSetListener,
                        course.getCourse().getAnticipatedEndDate().getYear(),
                        course.getCourse().getAnticipatedEndDate().getMonthValue()-1,
                        course.getCourse().getAnticipatedEndDate().getDayOfMonth());
            } else if (selectedTerm != null) {
                endDatePickerDialog = new DatePickerDialog(this, onEndDateSetListener,
                        selectedTerm.getStartDate().getYear(),
                        selectedTerm.getStartDate().plusMonths(1).getMonthValue()-1,
                        selectedTerm.getStartDate().getDayOfMonth());
            } else {
                endDatePickerDialog = new DatePickerDialog(this, onEndDateSetListener,
                        LocalDate.now().getYear(),
                        LocalDate.now().plusMonths(1).getMonthValue()-1,
                        LocalDate.now().getDayOfMonth());

            }
            endDatePickerDialog.show();
        });

        cancelBtn.setOnClickListener(v -> {
            this.setResult(RESULT_CANCELED);
            finish();
        });

        submitBtn.setOnClickListener(v -> {
            boolean validated = validateCourse(activityMode);
            if (validated) {
                editCourse.setTitle(titleEditText.getText().toString());
                editCourse.setTermId(selectedTerm.getId());
                editCourse.setStartDate(LocalDate
                        .parse(startDateEditText.getText(), DateTimeUtils.SHORT_DATE));
                editCourse.setAnticipatedEndDate(LocalDate
                        .parse(endDateEditText.getText(), DateTimeUtils.SHORT_DATE));
                editCourse.setStatus(selectedStatus);
                if (activityMode == 0 ) {
                    viewModel.updateCourse(editCourse);
                } else {
                    viewModel.insertCourse(editCourse);
                }
                this.setResult(RESULT_OK);
                finish();
            }
        });
    }

    private int getStatusPosition() {
        switch (course.getCourse().getStatus()) {
            case PLAN_TO_TAKE:
                return 0;
            case IN_PROGRESS:
                return 1;
            case COMPLETED:
                return 2;
            case DROPPED:
                return 3;
        }
        return 0;
    }

    private boolean validateCourse(int activityMode) {
        boolean titleValid;
        boolean termValid;
        boolean startDateValid;
        boolean endDateValid;
        boolean statusValid;

        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleValid = false;
            titleErrorTextView.setText(R.string.course_name_required);
        } else {
            titleValid = true;
            titleErrorTextView.setText("");
        }

        if (selectedTerm != null) {
            termValid = true;
            termErrorTextView.setText("");
        } else {
            termValid = false;
            termErrorTextView.setText(R.string.term_required);
        }

        if (!DateTimeUtils.dateStringIsValid(startDateEditText.getText().toString())) {
            startDateValid = false;
            startDateErrorTextView.setText(R.string.enter_valid_date);
        } else {
            LocalDate courseStart = LocalDate
                    .parse(startDateEditText.getText(),DateTimeUtils.SHORT_DATE);
            if (courseDateWithinTerm(courseStart)) {
                startDateValid = true;
                startDateErrorTextView.setText("");
            } else {
                startDateValid = false;
                startDateErrorTextView.setText(R.string.date_not_in_term);
            }
        }

        if (!DateTimeUtils.dateStringIsValid(endDateEditText.getText().toString())) {
            endDateValid = false;
            endDateErrorTextView.setText(R.string.enter_valid_date);
        } else {
            LocalDate courseEnd = LocalDate
                    .parse(endDateEditText.getText(),DateTimeUtils.SHORT_DATE);
            if (courseDateWithinTerm(courseEnd)) {
                endDateValid = true;
                endDateErrorTextView.setText("");
            } else {
                endDateValid = false;
                endDateErrorTextView.setText(R.string.date_not_in_term);
            }
        }

        if (startDateValid && endDateValid) {
            LocalDate courseStart = LocalDate
                    .parse(startDateEditText.getText(),DateTimeUtils.SHORT_DATE);
            LocalDate courseEnd = LocalDate
                    .parse(endDateEditText.getText(),DateTimeUtils.SHORT_DATE);
            if (courseStart.isBefore(courseEnd)) {
                startDateValid = true;
                endDateValid = true;
                startDateErrorTextView.setText("");
                endDateErrorTextView.setText("");
            } else {
                startDateValid = false;
                endDateValid = false;
                startDateErrorTextView.setText(R.string.start_date_after_end_date);
                endDateErrorTextView.setText(R.string.end_date_before_start_date);
            }
        }

        if (selectedStatus != null) {
            statusValid = true;
            statusErrorTextView.setText("");
        } else {
            statusValid = false;
            statusErrorTextView.setText(R.string.status_required);
        }

        return titleValid && termValid && startDateValid && endDateValid && statusValid;
    }

    private boolean courseDateWithinTerm(LocalDate date) {
        LocalDate termStart = selectedTerm.getStartDate();
        LocalDate termEnd = selectedTerm.getEndDate();

        try {
            return DateTimeUtils.dateIsBetween(date, termStart, termEnd);
        } catch (DateTimeUtilException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private DatePickerDialog.OnDateSetListener onStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startDateEditText.setText(LocalDate.of(year, month+1, dayOfMonth)
                    .format(DateTimeUtils.SHORT_DATE));
        }
    };

    private DatePickerDialog.OnDateSetListener onEndDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            endDateEditText.setText(LocalDate.of(year, month+1, dayOfMonth)
                    .format(DateTimeUtils.SHORT_DATE));
        }
    };
}
