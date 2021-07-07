package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.data.Competition;
import com.chrisvanry.weightlosscontest.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    // TODO hamburger menu
    // TODO back button should return to HOME

    private TextView textViewCurrentComp;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private String firebaseUserId = firebaseUser.getUid();

    // User object
    private User user = new User();

    // Competition object
    private Competition comp = new Competition();

    // debug
    public String userCompetitionId = "unchanged";
    public String userEmail = "unchanged";
    public String userFirstName = "unchanged";
    public String userLastName = "unchanged";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textViewCurrentComp = findViewById(R.id.textViewCurrentComp);
        Button buttonCompetition = findViewById(R.id.buttonCompetition);
        Button buttonCreateJoinComp = findViewById(R.id.buttonCreateJoinComp);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        //OnClick listener for logout button
        buttonLogout.setOnClickListener(v -> {
            // Logout and redirect to login screen
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            Toast.makeText(Home.this, "Logout successful", Toast.LENGTH_LONG).show();
        });

        getUserData();

        // debug log
        Log.d("DEBUG", firebaseUserId);
        Log.d("DEBUG", userCompetitionId+userEmail+userFirstName+userLastName);

        // compId = Integer.parseInt(user.getCompetitionId());

        // If user not enrolled, show text and hide details button
        // if (compId == 0){
            // textViewCurrentComp.setText("- Not Enrolled -");
            // buttonCompetition.setVisibility(View.GONE);

            // If user enrolled, display comp name and hide create/join button
        // } else {
            // getCompData();
            // compName = comp.getName();
            // textViewCurrentComp.setText(compName);
            // buttonCreateJoinComp.setVisibility(View.GONE);
        // }

    }

    // get User data from Firebase
    private void getUserData() {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    user = snapshot.getValue(User.class);
//                }
//                Toast.makeText(Home.this, "Database success", Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Home.this, "Database error", Toast.LENGTH_LONG).show();
//            }
//        });
//
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userCompetitionId = dataSnapshot.child("competitionId").getValue().toString();
                userEmail = dataSnapshot.child("email").getValue().toString();
                userFirstName = dataSnapshot.child("firstName").getValue().toString();
                userLastName = dataSnapshot.child("lastName").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                throw error.toException();
            }
        });
    }

    // get Comp data from Firebase
    private void getCompData() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Competitions");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    comp = snapshot.getValue(Competition.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}