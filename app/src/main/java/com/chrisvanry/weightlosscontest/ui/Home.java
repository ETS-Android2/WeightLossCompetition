package com.chrisvanry.weightlosscontest.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    // TODO hamburger menu
    // TODO back button should return to HOME

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
}