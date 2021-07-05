package com.chrisvanry.weightlosscontest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisvanry.weightlosscontest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// TODO Lock orientation to portrait

public class Login extends AppCompatActivity {

    private TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        TextView textViewSignUp = findViewById(R.id.signUpText);
        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();

        //OnClick listener for signup text - direct to signup activity
        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for login button
        buttonLogin.setOnClickListener(v -> {
            loginUser();
        });

    }

    private void loginUser() {

        String email = textInputEditTextEmail.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();

        // input validation - field not empty
        if(email.isEmpty()) {
            textInputEditTextEmail.setError("Email address is required");
            textInputEditTextEmail.requestFocus();
            return;
        }

        // input validation - valid email
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEditTextEmail.setError("Valid email address is required");
            textInputEditTextEmail.requestFocus();
            return;
        }

        // input validation - field not empty
        if(password.isEmpty()) {
            textInputEditTextPassword.setError("Password is required");
            textInputEditTextPassword.requestFocus();
            return;
        }

        // input validation - valid password
        if(password.length() < 6) {
            textInputEditTextPassword.setError("Password must be at least 6 characters");
            textInputEditTextPassword.requestFocus();
            return;
        }

        // Display progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Firebase user login
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    //redirect to home activity
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Login.this, "Login failed - check credentials and try again", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });

    }

}