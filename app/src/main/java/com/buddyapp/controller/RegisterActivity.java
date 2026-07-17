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

public class RegisterActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmInput = findViewById(R.id.confirmInput);
        Button registerButton = findViewById(R.id.registerButton);
        TextView backToLoginText = findViewById(R.id.backToLoginText);

        registerButton.setOnClickListener(v -> register());
        backToLoginText.setOnClickListener(v -> finish());
    }

    private void register() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirm = confirmInput.getText().toString();

        if (!Validators.hasMinLength(username, Constants.MIN_USERNAME_LENGTH)) {
            toast("Username must be at least " + Constants.MIN_USERNAME_LENGTH + " characters");
            return;
        }
        if (!Validators.hasMinLength(password, Constants.MIN_PASSWORD_LENGTH)) {
            toast("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            return;
        }
        if (!password.equals(confirm)) {
            toast(getString(R.string.error_password_mismatch));
            return;
        }
        if (db.getUserCount() > 0) {
            toast("An account already exists on this device");
            return;
        }

        // Special technique: never store the password directly.
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);
        long userId = db.insertUser(new User(username, hash, salt));
        if (userId == -1) {
            toast("Could not create the account");
            return;
        }

        session.setLoggedIn((int) userId);
        Intent intent = new Intent(this, FriendListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
