package com.chrisvanry.weightlosscompetition.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.chrisvanry.weightlosscompetition.R;
import com.chrisvanry.weightlosscompetition.activities.Login;
import com.chrisvanry.weightlosscompetition.data.Competition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO settings button
// TODO manage options for created competitions

public class CreateComp extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ViewDatabase";

    private TextInputEditText textInputEditTextCompName, textInputEditTextCompStartDate, textInputEditTextCompLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comp);

        // Nav buttons
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // input fields
        textInputEditTextCompName = findViewById(R.id.compName);
        textInputEditTextCompStartDate = findViewById(R.id.compStartDate);
        textInputEditTextCompLength = findViewById(R.id.compLength);

        Button buttonCreateComp = findViewById(R.id.buttonCreateComp);

        // alert message on open
        openMessage();

        // OnClick listener for home button
        buttonHome.setOnClickListener(v -> {
            // direct to home screen
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for settings button
        buttonSettings.setOnClickListener(v -> {
            // direct to comp list activity
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        });

        // OnClick listener for logout button
        buttonLogout.setOnClickListener(v -> {
            // Logout and redirect to login screen
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // OnFocus listener for date input
        textInputEditTextCompStartDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog();
                }
            }
        });

        // Move to next text input after date selected
        textInputEditTextCompStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textInputEditTextCompStartDate.getText().toString().length() > 1) {
                    textInputEditTextCompLength.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // OnClick listener for create competition button
        buttonCreateComp.setOnClickListener(v -> {
            registerComp();
        });

    }

    public void openMessage() {
        CreateCompDialog dialog = new CreateCompDialog();
        dialog.show(getSupportFragmentManager(), "create comp message");
    }

    // datepicker pop-up
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                );
        datePickerDialog.show();
    }

    // Get selected data from datepicker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Adjust offset
        month += 1;
        String date =  month + "/" + dayOfMonth + "/" + year;
        textInputEditTextCompStartDate.setText(date);
    }

    private void registerComp() {

        // Firebase stuff
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();
        String key = myRef.push().getKey();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        String ownerId = userID;

        String compName = textInputEditTextCompName.getText().toString().trim();
        String compStartDate = textInputEditTextCompStartDate.getText().toString().trim();
        String compLength = textInputEditTextCompLength.getText().toString().trim();
        String memberCount = "1";
        String competitionId = key;

        // input validation - field not empty
        if(compName.isEmpty()) {
            textInputEditTextCompName.setError("Competition name is required");
            textInputEditTextCompName.requestFocus();
            return;
        }

        // input validation - field not empty
        if(compStartDate.isEmpty()) {
            textInputEditTextCompStartDate.setError("Competition start date is required");
            textInputEditTextCompStartDate.requestFocus();
            return;
        }

        // input validation - field not empty
        if(compLength.isEmpty()) {
            textInputEditTextCompLength.setError("Competition length is required");
            textInputEditTextCompLength.requestFocus();
            return;
        }

        // input validation - number input only
        String regex = "^[0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(compLength);
        if(!matcher.matches()) {
            textInputEditTextCompLength.setError("Number-only input required");
            textInputEditTextCompLength.requestFocus();
            return;
        }

        // Create user object with user input
        Competition competition = new Competition(ownerId, compName, compStartDate, compLength, memberCount, competitionId);

        // Write object values to Firebase
        myRef.child("Competitions").child(key).setValue(competition);

        // Create first weight entry with 0 value
        String week1 = "1";
        myRef.child("Entries").child(key).child(userID).child(week1).setValue("0");

        // Update user competitionID in Firebase
        myRef.child("Users").child(userID).child("competitionId").setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    toastMessage("Competition creation successful");

                    // Redirect to home screen
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();

                } else {
                    toastMessage("Competition creation failed - try again");
                }
            }
        });

    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}