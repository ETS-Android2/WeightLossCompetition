package com.chrisvanry.weightlosscontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

// TODO Forgot password link

public class Login extends AppCompatActivity {

    TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    Button buttonLogin;
    TextView textViewSignUp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignUp = findViewById(R.id.signUpText);
        progressBar = findViewById(R.id.progress);

        //OnClick listener for signup text - direct to signup activity
        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(intent);
            finish();
        });

        // OnClick listener for login button
        buttonLogin.setOnClickListener(v -> {

            // Fetch input data
            String email, password;
            email = String.valueOf(textInputEditTextEmail.getText());
            password = String.valueOf(textInputEditTextPassword.getText());

            // Validate fields are not empty
            if(!email.equals("") && !password.equals("")) {

                // Display progress wheel
                progressBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.post(() -> {

                    String[] field = new String[2];
                    field[0] = "email";
                    field[1] = "password";

                    String[] data = new String[2];
                    data[0] = email;
                    data[1] = password;

                    // private database server - login
                    PutData putData = new PutData("https://chrisvanry.com/app-assets/login-register/login.php", "POST", field, data);

                    if (putData.startPut()) {

                        if (putData.onComplete()) {

                            progressBar.setVisibility(View.GONE);

                            String result = putData.getResult();

                            // Success message from login.php
                            if(result.equals("Login successful")) {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                                // Advance to HOME screen
                                Intent intent = new Intent(getApplicationContext(), Home.class);
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