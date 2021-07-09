package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
    // TODO proper hide/view of buttons

    private static final String TAG = "ViewDatabase";

    private TextView textViewCurrentComp;

    // Firebase stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button buttonLogout = findViewById(R.id.buttonLogout);
        textViewCurrentComp = findViewById(R.id.textViewCurrentComp);
        Button buttonCompetition = findViewById(R.id.buttonCompetition);
        Button buttonJoinComp = findViewById(R.id.buttonJoinComp);
        Button buttonCreateComp = findViewById(R.id.buttonCreateComp);

        // Declare the database reference object - used to access database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully logged in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully logged out.");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method will read the database once when the activity starts
                // and whenever any changes are made, returns compID
                int compID = showData(dataSnapshot);
                // If user not enrolled, show text and hide details button
                if (compID == 0){
                    textViewCurrentComp.setText("- Not Enrolled -");
                    buttonCompetition.setVisibility(View.GONE);

                    // If user enrolled, display comp name and hide create/join button
                } else {
                    textViewCurrentComp.setText("Example Comp");
                    buttonJoinComp.setVisibility(View.GONE);
                    buttonCreateComp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //OnClick listener for logout button
        buttonLogout.setOnClickListener(v -> {
            // Logout and redirect to login screen
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

    }

    private int showData(DataSnapshot dataSnapshot) {

        int compID = 0;

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            User currentUser = new User();
            currentUser.setFirstName(ds.child(userID).getValue(User.class).getFirstName()); // set the first name
            currentUser.setLastName(ds.child(userID).getValue(User.class).getLastName()); // set the last name
            currentUser.setEmail(ds.child(userID).getValue(User.class).getEmail()); // set the email
            currentUser.setCompetitionId(ds.child(userID).getValue(User.class).getCompetitionId()); // set the comp ID

            //display all the information
            Log.d(TAG, "showData: First name: " + currentUser.getFirstName());
            Log.d(TAG, "showData: Last name: " + currentUser.getLastName());
            Log.d(TAG, "showData: Email: " + currentUser.getEmail());
            Log.d(TAG, "showData: Comp ID: " + currentUser.getCompetitionId());

//            ArrayList<String> array  = new ArrayList<>();
//            array.add(uInfo.getFirstName());
//            array.add(uInfo.getLastName());
//            array.add(uInfo.getEmail());
//            array.add(uInfo.getCompetitionId());
//            ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
//            mListView.setAdapter(adapter);

            compID = Integer.parseInt(currentUser.getCompetitionId());

        }

        return compID;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}