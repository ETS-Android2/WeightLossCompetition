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

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    // TODO hamburger menu
    // TODO back button should return to HOME

    private TextView textViewCurrentComp;

    // Firebase
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private final String firebaseUserId = firebaseUser.getUid();

    // Competition object
    private Competition comp = new Competition();

    final ArrayList<String> list = new ArrayList<String>();

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Retrieve user data from Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User userData = snapshot.getValue(User.class);
                    String firstName = userData.getFirstName();
                    String lastName = userData.getLastName();
                    String email = userData.getEmail();
                    String competitionId = userData.getCompetitionId();
                    list.add(firstName);
                    list.add(lastName);
                    list.add(email);
                    list.add(competitionId);
                    // debug
                    list.add("success");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //debug
                list.add("error");
                throw error.toException();
            }
        });

        // debug
        list.add("outside scope");

        // debug log
        Log.d("DEBUGOBJECT", firebaseUserId);
        Log.d("DEBUGOBJECT", list.toString());

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

}