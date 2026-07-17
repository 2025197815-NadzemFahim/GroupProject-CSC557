package com.buddyapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buddyapp.R;
import com.buddyapp.model.DatabaseHelper;
import com.buddyapp.model.User;
import com.buddyapp.util.Constants;
import com.buddyapp.util.PasswordHasher;
import com.buddyapp.util.SessionManager;
import com.buddyapp.util.Validators;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;

    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            goToFriendList();
            return;
        }

        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView goToRegisterText = findViewById(R.id.goToRegisterText);

        loginButton.setOnClickListener(v -> login());
        goToRegisterText.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (!Validators.hasMinLength(username, Constants.MIN_USERNAME_LENGTH)) {
            toast("Username must be at least " + Constants.MIN_USERNAME_LENGTH + " characters");
            return;
        }
        if (!Validators.hasMinLength(password, Constants.MIN_PASSWORD_LENGTH)) {
            toast("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        User user = db.getUserByUsername(username);
        if (user == null) {
            toast("Account not found");
            return;
        }
        if (!PasswordHasher.verify(password, user.getSalt(), user.getPasswordHash())) {
            toast("Incorrect password");
            return;
        }

        session.setLoggedIn(user.getUserId());
        goToFriendList();
    }

    private void goToFriendList() {
        Intent intent = new Intent(this, FriendListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
