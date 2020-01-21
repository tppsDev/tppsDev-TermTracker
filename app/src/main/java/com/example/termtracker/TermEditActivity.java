package com.example.termtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.termtracker.entity.Term;
import com.example.termtracker.util.DateTimeUtilException;
import com.example.termtracker.util.DateTimeUtils;
import com.example.termtracker.view_model.TermEditViewModel;

import java.time.LocalDate;
import java.util.List;

public class TermEditActivity extends AppCompatActivity {
    TermEditViewModel editTermViewModel;
    Term term;
    List<Term> allTerms;
    EditText titleEditText;
    EditText startDateEditText;
    TextView endDateTextView;
    TextView titleErrorTextField;
    TextView startDateErrorTextField;
    TextView endDateErrorTextField;
    ImageButton startDatePickerBtn;
    Button cancelBtn;
    Button submitBtn;

    DatePickerDialog startDatePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);
        titleEditText = findViewById(R.id.editTermTitleEditText);
        startDateEditText = findViewById(R.id.editTermStartDateEditText);
        endDateTextView = findViewById(R.id.editTermEndDateTextView);
        titleErrorTextField = findViewById(R.id.editTermTitleErrorTextView);
        startDateErrorTextField = findViewById(R.id.editTermStartDateErrorTextView);
        endDateErrorTextField = findViewById(R.id.editTermEndDateErrorTextView);
        startDatePickerBtn = findViewById(R.id.editTermStartDatePickerImageButton);
        cancelBtn = findViewById(R.id.editTermCancelBtn);
        submitBtn = findViewById(R.id.editTermSubmitBtn);

        editTermViewModel = ViewModelProviders.of(this).get(TermEditViewModel.class);

        Intent intent = getIntent();
        final int activityMode = intent.getIntExtra(getString(R.string.edit_term_mode_key), 0);
        if (activityMode == 0) {
            term = intent.getParcelableExtra(getString(R.string.term_object_key));
            titleEditText.setText(term.getTitle());
            startDateEditText.setText(term.getStartDate().format(DateTimeUtils.SHORT_DATE));
            endDateTextView.setText(term.getEndDate().format(DateTimeUtils.SHORT_DATE));
            titleEditText.requestFocus();
        } else {
            term = new Term();
            titleEditText.requestFocus();
        }

        editTermViewModel.getAllTerms().observe(this, new Observer<List<Term>>() {
            @Override
            public void onChanged(List<Term> terms) {
                allTerms = terms;
            }
        });

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
                if (DateTimeUtils.dateStringIsValid(dateText)) {
                    startDateErrorTextField.setText("");
                    LocalDate endDate = LocalDate
                            .parse(dateText, DateTimeUtils.SHORT_DATE).plusMonths(6).minusDays(1);
                    endDateTextView.setText(endDate.format(DateTimeUtils.SHORT_DATE));
                } else {
                    startDateErrorTextField.setText(R.string.enter_valid_date);
                }
            }
        });

        startDatePickerBtn.setOnClickListener(v -> {
            if (activityMode == 0) {
                startDatePickerDialog = new DatePickerDialog(this, onStartDateSetListener,
                        term.getStartDate().getYear(),
                        term.getStartDate().getMonthValue() -1,
                        term.getStartDate().getDayOfMonth());
            } else {
                startDatePickerDialog = new DatePickerDialog(this, onStartDateSetListener,
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue() -1,
                        LocalDate.now().getDayOfMonth());
            }
            startDatePickerDialog.show();

        });

        cancelBtn.setOnClickListener(v -> {
            this.setResult(RESULT_CANCELED);
            finish();
        });

        submitBtn.setOnClickListener(v -> {
            boolean validated = validateTerm(activityMode);
            if (validated) {
                term.setTitle(titleEditText.getText().toString());
                term.setStartDate(LocalDate
                        .parse(startDateEditText.getText(),DateTimeUtils.SHORT_DATE));
                term.setEndDate(LocalDate
                        .parse(endDateTextView.getText(),DateTimeUtils.SHORT_DATE));
                if (activityMode == 0) {
                    editTermViewModel.updateTerm(term);
                } else {
                    editTermViewModel.insertTerm(term);
                }
                this.setResult(RESULT_OK);
                finish();
            }
        });
    }

    private boolean validateTerm(int activityMode) {
        boolean titleValid;
        boolean startDateValid;
        boolean endDateValid;

        if (titleEditText.getText().toString().trim().isEmpty()) {
            titleValid = false;
            titleErrorTextField.setText(R.string.term_name_required);
        } else {
            titleValid = true;
            titleErrorTextField.setText("");
        }

        if (!DateTimeUtils.dateStringIsValid(startDateEditText.getText().toString())) {
            startDateValid = false;
            startDateErrorTextField.setText(R.string.enter_valid_date);
        } else {
            LocalDate startDate = LocalDate
                    .parse(startDateEditText.getText(),DateTimeUtils.SHORT_DATE);

            if (checkOverlap(startDate, activityMode)) {
                startDateValid = false;
                startDateErrorTextField.setText(R.string.term_date_overlap);
            } else {
                startDateValid = true;
                startDateErrorTextField.setText("");
            }
        }

        if (startDateValid) {
            LocalDate endDate = LocalDate.parse(endDateTextView.getText(),DateTimeUtils.SHORT_DATE);

            if (checkOverlap(endDate, activityMode)) {
                endDateValid = false;
                endDateErrorTextField.setText(R.string.term_date_overlap);
            } else {
                endDateValid = true;
                endDateErrorTextField.setText("");
            }

        } else {
            endDateValid = false;
        }

        return titleValid && startDateValid && endDateValid;
    }

    private boolean checkOverlap(LocalDate date, int activityMode) {
        if (activityMode == 0) {
            return checkEditTermOverlap(date, term.getId());
        } else {
            return checkAddTermOverlap(date);
        }
    }

    private boolean checkEditTermOverlap(LocalDate date, int termId) {
        boolean overlap = false;

        for (int i = 0; i < allTerms.size(); i++) {
            LocalDate firstDate = allTerms.get(i).getStartDate();
            LocalDate secondDate = allTerms.get(i).getEndDate();
            try {
                if(DateTimeUtils.dateIsBetween(date,firstDate,secondDate)
                        && allTerms.get(i).getId() != termId) {
                    overlap = true;
                    break;
                }
            } catch (DateTimeUtilException e) {
                e.printStackTrace();
                overlap = true;
            }
        }

        return overlap;
    }

    private boolean checkAddTermOverlap(LocalDate date) {
        boolean overlap = false;

        for (int i = 0; i < allTerms.size(); i++) {
            LocalDate firstDate = allTerms.get(i).getStartDate();
            LocalDate secondDate = allTerms.get(i).getEndDate();
            try {
                if(DateTimeUtils.dateIsBetween(date,firstDate,secondDate)) {
                    overlap = true;
                    break;
                }
            } catch (DateTimeUtilException e) {
                e.printStackTrace();
                overlap = true;
            }
        }

        return overlap;
    }

    private DatePickerDialog.OnDateSetListener onStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    startDateEditText.setText(LocalDate.of(year, month+1, dayOfMonth)
                            .format(DateTimeUtils.SHORT_DATE));
                }
            };
}
