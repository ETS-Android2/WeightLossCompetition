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
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
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

public class Settings extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private User currentUser;
    private String currentUserId;
    private String currentCompId;
    private Button buttonLeaveComp;

    // Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Nav buttons
        Button buttonHome = findViewById(R.id.buttonHome);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // Setting buttons
        buttonLeaveComp = findViewById(R.id.buttonLeaveComp);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUserId = user.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            // Reads database once on creation and each time a change is made
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get current user and comp ID and name
                currentUser = getUserData(dataSnapshot);
                currentCompId = currentUser.getCompetitionId();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // OnClick listener for home button
        buttonHome.setOnClickListener(v -> {
            // direct to home activity
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

        // OnClick listener for leave comp button
        buttonLeaveComp.setOnClickListener(v -> {

            String notEnrolled = "not enrolled";
            // User comp enrollment status
            if (!currentCompId.equals(notEnrolled)) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // Join button clicked
                                leaveCurrentComp();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                // Cancel button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Leave Competition");
                builder.setMessage("Are you sure?").setPositiveButton("Leave", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();
            } else {
                toastMessage("User not currently enrolled in competition");
            }
        });
    }

    // get user data for current user
    private User getUserData(DataSnapshot dataSnapshot) {

        User currentUser = new User();

        currentUser.setFirstName(dataSnapshot.child("Users").child(currentUserId).child("firstName").getValue().toString());
        currentUser.setLastName(dataSnapshot.child("Users").child(currentUserId).child("lastName").getValue().toString());
        currentUser.setEmail(dataSnapshot.child("Users").child(currentUserId).child("email").getValue().toString());
        currentUser.setCompetitionId(dataSnapshot.child("Users").child(currentUserId).child("competitionId").getValue().toString());

        // display all the information
        Log.d(TAG, "showData User: First name: " + currentUser.getFirstName());
        Log.d(TAG, "showData User: Last name: " + currentUser.getLastName());
        Log.d(TAG, "showData User: Email: " + currentUser.getEmail());
        Log.d(TAG, "showData User: Comp ID: " + currentUser.getCompetitionId());

        return currentUser;
    }

    private void leaveCurrentComp() {

        myRef.child("Users").child(currentUserId).child("competitionId").setValue("not enrolled").addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    toastMessage("Competition un-enrollment successful");

                    // hide button
                    buttonLeaveComp.setEnabled(false);

                } else {
                    toastMessage("Competition un-enrollment failed - try again");
                }
            }
        });

    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}