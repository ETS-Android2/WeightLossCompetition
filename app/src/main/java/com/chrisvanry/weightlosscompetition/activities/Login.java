package com.chrisvanry.weightlosscompetition.activities;

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

import com.chrisvanry.weightlosscompetition.R;
import com.chrisvanry.weightlosscompetition.ui.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// TODO: ability to press enter to login

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        Button buttonLogin = findViewById(R.id.button_login);
        TextView textViewRegisterUser = findViewById(R.id.text_register_user);
        progressBar = findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();

        // OnClick listener for signup text - direct to register activity
        textViewRegisterUser.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
        });

        // OnClick listener for login button
        buttonLogin.setOnClickListener(v -> {

            if (validate()) {
                loginUser();
            }
            else {
                toastMessage("Login failed - check errors and try again");
            }
        });

    }

    private boolean validate() {

        boolean valid = true;
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Valid email address is required");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            editTextPassword.setError("Password must be at least 8 characters");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }

    private void loginUser() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Display progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Firebase user login
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    // redirect to home activity
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    toastMessage("Login successful");
                    finish();
                } else {
                    toastMessage("Login failed - try again");
                }
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}