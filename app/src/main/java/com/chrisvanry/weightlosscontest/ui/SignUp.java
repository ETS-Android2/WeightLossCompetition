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
import com.chrisvanry.weightlosscontest.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private TextInputEditText textInputEditTextFirstName, textInputEditTextLastName, textInputEditTextEmail, textInputEditTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextFirstName = findViewById(R.id.firstName);
        textInputEditTextLastName = findViewById(R.id.lastName);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        Button buttonSignup = findViewById(R.id.buttonSignup);
        TextView textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();

        //OnClick listener for login text - direct to login activity
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for sign up button
        buttonSignup.setOnClickListener(v -> {
            registerUser();
        });

    }

    private void registerUser() {

        String firstName = textInputEditTextFirstName.getText().toString().trim();
        String lastName = textInputEditTextLastName.getText().toString().trim();
        String email = textInputEditTextEmail.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();
        // Default is 0 until user joins competition
        String competitionId = "0";

        // input validation - field not empty
        if(firstName.isEmpty()) {
            textInputEditTextFirstName.setError("First name is required");
            textInputEditTextFirstName.requestFocus();
            return;
        }

        // input validation - field not empty
        if(lastName.isEmpty()) {
            textInputEditTextLastName.setError("Last name is required");
            textInputEditTextLastName.requestFocus();
            return;
        }

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

        // Firebase create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            User user = new User(firstName, lastName, email, competitionId);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "User creation successful", Toast.LENGTH_LONG).show();

                                        // Redirect to login screen
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(SignUp.this, "User creation failed - try again", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(SignUp.this, "User creation failed - try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }
}