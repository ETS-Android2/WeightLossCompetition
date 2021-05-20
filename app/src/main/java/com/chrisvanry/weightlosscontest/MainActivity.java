package com.chrisvanry.weightlosscontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Variables with private modifier - only accessible inside of this class
    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;

    private String Email = "Admin";
    private String Password = "12345678";

    boolean isValid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variables attached to layout items
        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);

        // OnClick will validate input email and password
        eLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String inputEmail = eEmail.getText().toString();
                String inputPassword = ePassword.getText().toString();

                if (inputEmail.isEmpty() || inputPassword.isEmpty())
                {
                    // popup message
                    Toast.makeText(MainActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();

                } else {

                    // validate input email and password
                    isValid = validate(inputEmail, inputPassword);

                    if (!isValid) {

                        Toast.makeText(MainActivity.this, "Incorrect login information.", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(MainActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();

                        // TODO: Add new activity
                        // Launches the new activity
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);

                    }

                }
            }
        });

    }

    private boolean validate(String email, String password) {

        if (email.equals(Email) && password.equals(Password)) {
            return true;
        }

        return false;

    }

}