package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class JoinComp extends AppCompatActivity {

    // widgets
    RecyclerView recyclerView;

    // Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    // variables
    private ArrayList<String> competitionsList;
    private RecyclerViewAdapter recyclerViewAdapter;

    private static final String TAG = "JoinCompActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_comp);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Competitions");

        competitionsList = new ArrayList<>();

        // Clear arraylist
        clearList();

        // Get Data Method and store in arraylist
        getDataFromFirebase(competitionsList);

//        // Nav buttons
//        Button buttonHome = findViewById(R.id.buttonHome);
//        // Button buttonSettings = findViewById(R.id.buttonSettings);
//        Button buttonLogout = findViewById(R.id.buttonLogout);
//
//        // OnClick listener for home button
//        buttonHome.setOnClickListener(v -> {
//            // direct to home screen
//            Intent intent = new Intent(getApplicationContext(), Home.class);
//            startActivity(intent);
//            finish();
//        });
//
//        // OnClick listener for logout button
//        buttonLogout.setOnClickListener(v -> {
//            // Logout and redirect to login screen
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        });

    }

    private void getDataFromFirebase(ArrayList competitionsList) {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                clearList();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Competition competition = snapshot.getValue(Competition.class);
                    competitionsList.add(competition.getName());
                    Log.d(TAG, "getDataFromFirebase: name added to list: " + competition.getName());
                }

                Log.d(TAG, "getDataFromFirebase: list contents: " + competitionsList.toString());

                recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(), competitionsList);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();

                Log.d(TAG, "getDataFromFirebase: recyclerViewAdapter called ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearList() {
        if (competitionsList != null) {
            competitionsList.clear();
            Log.d(TAG, "clearList: list cleared.");

            if (recyclerViewAdapter != null) {
                recyclerViewAdapter.notifyDataSetChanged();
                Log.d(TAG, "clearList: notifyDataSetChanged");
            }
        }
        competitionsList = new ArrayList<>();
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}