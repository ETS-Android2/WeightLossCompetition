package com.chrisvanry.weightlosscontest.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.google.firebase.auth.FirebaseAuth;

public class JoinComp extends AppCompatActivity {

    private static final String TAG = "ViewDatabase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_comp);

        // Nav buttons
        Button buttonHome = findViewById(R.id.buttonHome);
        // Button buttonSettings = findViewById(R.id.buttonSettings);
        Button buttonLogout = findViewById(R.id.buttonLogout);

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

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}