package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.data.Competition;
import com.chrisvanry.weightlosscontest.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CompDetails extends AppCompatActivity {

    private static final String TAG = "CompDetailsActivity";

    private String compDetailsId;
    private String currentCompName;
    private String currentCompLength;
    private TextView textViewCurrentComp;
    private ProgressBar progressBar;
    private Button buttonJoinComp;
    private Button buttonRecordWeight;

    // Firebase stuff
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp_details);

        // Receive string from previous activity
        Intent intent = getIntent();
        compDetailsId = intent.getStringExtra("comp_id");
        Log.d(TAG, "Comp ID received: " + compDetailsId);

        // Nav buttons
        Button buttonHome = findViewById(R.id.buttonHome);
        // Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        textViewCurrentComp = findViewById(R.id.textViewCurrentComp);
        buttonJoinComp = findViewById(R.id.buttonJoinComp);
        buttonRecordWeight = findViewById(R.id.buttonRecordWeight);

        // progress bar
        progressBar = findViewById(R.id.progress);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // show progress bar while loading
        progressBar.setVisibility(View.VISIBLE);

        myRef.addValueEventListener(new ValueEventListener() {
            // Reads database once on creation and each time a change is made
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get current user and comp ID and name
                User currentUser = getUserData(dataSnapshot);
                Competition currentComp = getCompData(dataSnapshot, compDetailsId);
                currentCompName = currentComp.getName();
                currentCompLength = currentComp.getLength();

                // get chart data

                // Display comp name
                progressBar.setVisibility(View.GONE);
                textViewCurrentComp.setText(currentCompName);

                // display chart

                String userCompId = currentUser.getCompetitionId();
                String notEnrolled = "not enrolled";

                // User comp enrollment status
                if (userCompId.equals(notEnrolled)){
                    // If user not enrolled, show join button
                    buttonJoinComp.setVisibility(View.VISIBLE);
                } else if (userCompId.equals(compDetailsId)){
                    // If user enrolled in this comp, show record weight button
                    buttonRecordWeight.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // OnClick listener for home button
        buttonHome.setOnClickListener(v -> {
            // direct to home screen
            Intent newIntent = new Intent(getApplicationContext(), Home.class);
            startActivity(newIntent);
            finish();
        });

        // OnClick listener for logout button
        buttonLogout.setOnClickListener(v -> {
            // Logout and redirect to login screen
            FirebaseAuth.getInstance().signOut();
            Intent newIntent = new Intent(getApplicationContext(), Login.class);
            startActivity(newIntent);
            finish();
        });

        // OnClick listener for join comp button
        buttonJoinComp.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            joinCurrentComp(userID, compDetailsId, currentCompLength);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Join Competition");
            builder.setMessage("Join " + currentCompName + "?").setPositiveButton("Join", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        });

    }

    private User getUserData(DataSnapshot dataSnapshot) {

        User currentUser = new User();

        currentUser.setFirstName(dataSnapshot.child("Users").child(userID).child("firstName").getValue().toString());
        currentUser.setLastName(dataSnapshot.child("Users").child(userID).child("lastName").getValue().toString());
        currentUser.setEmail(dataSnapshot.child("Users").child(userID).child("email").getValue().toString());
        currentUser.setCompetitionId(dataSnapshot.child("Users").child(userID).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData User: First name: " + currentUser.getFirstName());
        Log.d(TAG, "showData User: Last name: " + currentUser.getLastName());
        Log.d(TAG, "showData User: Email: " + currentUser.getEmail());
        Log.d(TAG, "showData User: Comp ID: " + currentUser.getCompetitionId());

        return currentUser;
    }

    private Competition getCompData(DataSnapshot dataSnapshot, String compID) {

        Competition currentComp = new Competition();

        currentComp.setOwnerId(dataSnapshot.child("Competitions").child(compID).child("ownerId").getValue().toString());
        currentComp.setName(dataSnapshot.child("Competitions").child(compID).child("name").getValue().toString());
        currentComp.setStartDate(dataSnapshot.child("Competitions").child(compID).child("startDate").getValue().toString());
        currentComp.setLength(dataSnapshot.child("Competitions").child(compID).child("length").getValue().toString());
        currentComp.setCompetitionId(dataSnapshot.child("Competitions").child(compID).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData Comp: Owner ID: " + currentComp.getOwnerId());
        Log.d(TAG, "showData Comp: Comp name: " + currentComp.getName());
        Log.d(TAG, "showData Comp: Start date: " + currentComp.getStartDate());
        Log.d(TAG, "showData Comp: Length: " + currentComp.getLength());
        Log.d(TAG, "showData Comp: Comp ID: " + currentComp.getCompetitionId());

        return currentComp;
    }

    private void joinCurrentComp(String userID, String compDetailsId, String currentCompLength) {

        // Create 0 value entries for each week in Firebase
        int compLengthInt = Integer.parseInt(currentCompLength);
        String compWeek = "week";
        for (int i = 1; i <= compLengthInt; i++) {
            myRef.child("Entries").child(compDetailsId).child(userID).child("week" + i).setValue("0");
        }
        // write compID to user branch
        myRef.child("Users").child(userID).child("competitionId").setValue(compDetailsId).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    toastMessage("Competition enrollment successful");

                    // Redirect to home screen
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();

                } else {
                    toastMessage("Competition enrollment failed - try again");
                }
            }
        });

    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}