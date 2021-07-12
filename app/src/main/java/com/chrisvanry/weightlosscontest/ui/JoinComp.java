package com.chrisvanry.weightlosscontest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.data.Competition;
import com.chrisvanry.weightlosscontest.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinComp extends AppCompatActivity {

    private static final String TAG = "JoinCompActivity";

    private ArrayList<String> mCompNames = new ArrayList<>();

    // Firebase stuff
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_comp);
        Log.d(TAG, "onCreate: started");

        // Nav buttons
        Button buttonHome = findViewById(R.id.buttonHome);
        // Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference().child("Competitions");

        myRef.addValueEventListener(new ValueEventListener() {
            // Reads database once on creation and each time a change is made
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCompNames = getCompList(dataSnapshot);
                Log.d(TAG, "getCompList called");
                // Display recyclerview list of competitions
                initRecyclerView();
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

    }

    private ArrayList getCompList(DataSnapshot dataSnapshot) {

        ArrayList<String> compList = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Competition competition = snapshot.getValue(Competition.class);
            compList.add(competition.getName());
            Log.d(TAG, "compList add: " + competition.getName());
        }
        Log.d(TAG, "compList generated: " + compList.toString());
        return compList;
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: mCompNames contents: " + mCompNames.toString());
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mCompNames, this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}