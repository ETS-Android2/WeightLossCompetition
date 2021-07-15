package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.data.Competition;
import com.chrisvanry.weightlosscontest.data.User;
import com.chrisvanry.weightlosscontest.ui.Home;
import com.chrisvanry.weightlosscontest.ui.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecordWeight extends AppCompatActivity {

    private static final String TAG = "RecordWeight";

    private Button buttonHome;
    // private Button buttonSettings;
    private Button buttonLogout;
    private Button buttonRecordWeight;

    private TextView textViewCurrentComp;

    // drop down menu
    private Spinner spinner;

    private TextInputEditText textInputWeight;

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;;

    private String userId;
    private String compId;
    private String compLength;

    private List spinnerList;

    private String weekInput;
    private String weightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_weight);

        // Nav buttons
        buttonHome = findViewById(R.id.buttonHome);
        // Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonLogout = findViewById(R.id.buttonLogout);

        spinner = findViewById(R.id.weekSpinner);
        textInputWeight = findViewById(R.id.textInputWeight);
        buttonRecordWeight = findViewById(R.id.buttonRecordWeight);

        textViewCurrentComp = findViewById(R.id.textViewCurrentComp);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            // Reads database once on creation and each time a change is made
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // User object data from firebase
                User currentUser = getUserData(dataSnapshot);
                compId = currentUser.getCompetitionId();
                // Comp object data from firebase
                Competition currentComp = getCompData(dataSnapshot, compId);
                compLength = currentComp.getLength();
                textViewCurrentComp.setText(currentComp.getName());
                setSpinnerAdapter();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // OnClick listener for home button
        buttonHome.setOnClickListener(v -> {
            // direct to home screen
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for logout button
        buttonLogout.setOnClickListener(v -> {
            // Logout and redirect to login screen
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for record weight button
        buttonRecordWeight.setOnClickListener(v -> {

            // get input
            weekInput = spinner.getSelectedItem().toString().trim();
            weightInput = textInputWeight.getText().toString().trim();

            // input validation
            TextView errorText = (TextView)spinner.getSelectedView();
            if (spinner.getSelectedItem().toString().equals("Select week")){
                errorText.setText("Week selection required");
                errorText.setError("");
                return;
            }

            // input validation
            int weightInputInt = Integer.parseInt(weightInput);
            if (weightInputInt < 100) {
                textInputWeight.setError("Must be at least 100 lbs");
                textInputWeight.requestFocus();
                return;
            }

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            // Yes button clicked
                            recordWeightEntry(userId, compId);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            // Cancel button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Confirm Weight Entry");
            builder.setMessage("Submit " + weightInput + " for week " + weekInput + "?").setPositiveButton("Submit", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        });

    }

    private User getUserData(DataSnapshot dataSnapshot) {

        User currentUser = new User();

        currentUser.setFirstName(dataSnapshot.child("Users").child(userId).child("firstName").getValue().toString());
        currentUser.setLastName(dataSnapshot.child("Users").child(userId).child("lastName").getValue().toString());
        currentUser.setEmail(dataSnapshot.child("Users").child(userId).child("email").getValue().toString());
        currentUser.setCompetitionId(dataSnapshot.child("Users").child(userId).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData: First name: " + currentUser.getFirstName());
        Log.d(TAG, "showData: Last name: " + currentUser.getLastName());
        Log.d(TAG, "showData: Email: " + currentUser.getEmail());
        Log.d(TAG, "showData: Comp ID: " + currentUser.getCompetitionId());

        return currentUser;
    }

    private Competition getCompData(DataSnapshot dataSnapshot, String compId) {

        Competition currentComp = new Competition();

        currentComp.setName(dataSnapshot.child("Competitions").child(compId).child("name").getValue().toString());
        currentComp.setStartDate(dataSnapshot.child("Competitions").child(compId).child("startDate").getValue().toString());
        currentComp.setLength(dataSnapshot.child("Competitions").child(compId).child("length").getValue().toString());
        currentComp.setOwnerId(dataSnapshot.child("Competitions").child(compId).child("ownerId").getValue().toString());
        currentComp.setCompetitionId(dataSnapshot.child("Competitions").child(compId).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData: Comp name: " + currentComp.getName());
        Log.d(TAG, "showData: Start date: " + currentComp.getStartDate());
        Log.d(TAG, "showData: Length: " + currentComp.getLength());
        Log.d(TAG, "showData: Owner ID: " + currentComp.getOwnerId());
        Log.d(TAG, "showData: Comp ID: " + currentComp.getCompetitionId());

        return currentComp;
    }

    private void setSpinnerAdapter() {

        // spinner - week selector
        spinnerList = new ArrayList();
        spinnerList.add("Select week");

        // populate spinner array for every week
        int compLengthInt = Integer.parseInt(compLength);
        for (int i = 1; i <= compLengthInt; i++) {
            spinnerList.add(i);
        }
        Log.d(TAG, "Spinner data: " + spinnerList.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void recordWeightEntry(String userId, String compId) {

        // Write value to firebase
        myRef.child("Entries").child(compId).child(userId).child(weekInput).setValue(weightInput).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    toastMessage("Record weight successful");

                    // direct to comp details screen
                    Intent intent = new Intent(getApplicationContext(), CompDetails.class);
                    intent.putExtra("comp_id", compId);
                    startActivity(intent);
                    finish();

                } else {
                    toastMessage("Record weight failed - try again");
                }
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}