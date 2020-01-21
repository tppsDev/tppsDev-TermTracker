package com.example.termtracker;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserSetupActivity extends AppCompatActivity {
    SharedPreferences sPref;
    Editor editor;
    EditText nameEditText;
    TextView nameErrorTextView;
    Button cancelButton;
    Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        setContentView(R.layout.activity_user_setup);
        nameEditText = this.findViewById(R.id.userSetupNameEditText);
        nameErrorTextView = this.findViewById(R.id.userSetupNameErrorTextView);
        cancelButton = this.findViewById(R.id.userSetupCancelButton);
        submitButton = this.findViewById(R.id.userSetupSubmitButton);

        if (sPref.contains(getString(R.string.user_name_key))) {
            nameEditText.setText(sPref.getString(getString(R.string.user_name_key), null));
        }

        cancelButton.setOnClickListener(click -> {
            if (sPref.contains(getString(R.string.user_name_key))) {
                this.setResult(RESULT_CANCELED);
                finish();
            } else {
                nameErrorTextView.setText(R.string.valid_name_error);
            }

        });

        submitButton.setOnClickListener(click -> {
            if (!nameEditText.getText().toString().trim().isEmpty()) {
                editor = sPref.edit();
                editor.putString(getString(R.string.user_name_key),
                        nameEditText.getText().toString().trim());
                editor.apply();
                this.setResult(RESULT_OK);
                Log.d("Timmy Time", "onCreate: right before finish");
                finish();
                Log.d("Timmy Time", "onCreate:  right after finish");
            } else {
                nameErrorTextView.setText(R.string.valid_name_error);
            }
        });


    }
}
