package com.chrisvanry.weightlosscontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

// TODO password requirements
// TODO handle attempts to add existing email addresses

public class SignUp extends AppCompatActivity {

    TextInputEditText textInputEditTextFirstname, textInputEditTextLastname, textInputEditTextEmail, textInputEditTextPassword;
    Button buttonSignup;
    TextView textViewLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextFirstname = findViewById(R.id.firstname);
        textInputEditTextLastname = findViewById(R.id.lastname);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);

        //OnClick listener for login text - direct to login activity
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for sign up button
        buttonSignup.setOnClickListener(v -> {

            // Fetch input data
            String firstname, lastname, email, password;
            firstname = String.valueOf(textInputEditTextFirstname.getText());
            lastname = String.valueOf(textInputEditTextLastname.getText());
            email = String.valueOf(textInputEditTextEmail.getText());
            password = String.valueOf(textInputEditTextPassword.getText());

            // Validate fields are not empty
            if(!firstname.equals("") && !lastname.equals("") && !email.equals("") && !password.equals("")) {

                // Display progress wheel
                progressBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.post(() -> {

                    String[] field = new String[4];
                    field[0] = "firstname";
                    field[1] = "lastname";
                    field[2] = "email";
                    field[3] = "password";

                    String[] data = new String[4];
                    data[0] = firstname;
                    data[1] = lastname;
                    data[2] = email;
                    data[3] = password;

                    // private database server - signup
                    PutData putData = new PutData("https://chrisvanry.com/app-assets/login-register/signup.php", "POST", field, data);

                    if (putData.startPut()) {

                        if (putData.onComplete()) {

                            progressBar.setVisibility(View.GONE);

                            String result = putData.getResult();

                            // Success message from signup.php
                            if(result.equals("Sign up successful")) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                // Advance to login screen
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "All fields required", Toast.LENGTH_SHORT).show();
            }
        });

    }
}