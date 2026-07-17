package com.buddyapp.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buddyapp.R;
import com.buddyapp.model.DatabaseHelper;
import com.buddyapp.model.Friend;
import com.buddyapp.util.Constants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class FriendDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private int friendId;

    private ImageView friendPhoto;
    private TextView nameText;
    private TextView genderText;
    private TextView phoneText;
    private TextView emailText;
    private TextView addressText;
    private TextView stateText;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);

        db = new DatabaseHelper(this);
        friendId = getIntent().getIntExtra(Constants.EXTRA_FRIEND_ID, Constants.NO_ID);

        friendPhoto = findViewById(R.id.friendPhoto);
        nameText = findViewById(R.id.nameText);
        genderText = findViewById(R.id.genderText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        addressText = findViewById(R.id.addressText);
        stateText = findViewById(R.id.stateText);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Friend friend = db.getFriendById(friendId);
        if (friend == null) {
            finish();
            return;
        }
        bind(friend);
    }

    private boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddEditFriendActivity.class);
            intent.putExtra(Constants.EXTRA_FRIEND_ID, friendId);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        }
        return false;
    }

    private void bind(Friend friend) {
        toolbar.setTitle(friend.getName());
        nameText.setText(friend.getName());
        genderText.setText(friend.getGender());
        phoneText.setText(orNotProvided(friend.getPhone()));
        emailText.setText(orNotProvided(friend.getEmail()));
        addressText.setText(orNotProvided(buildAddress(friend)));
        stateText.setText(friend.getStateName() != null
                ? friend.getStateName()
                : getString(R.string.not_provided));

        if (friend.getPhotoUri() != null) {
            friendPhoto.setImageURI(Uri.parse(friend.getPhotoUri()));
        } else {
            friendPhoto.setImageResource(R.drawable.ic_person);
        }
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    db.deleteFriend(friendId);
                    Toast.makeText(this, R.string.friend_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }

    private String buildAddress(Friend friend) {
        List<String> lines = new ArrayList<>();
        addIfPresent(lines, friend.getAddressLine1());
        addIfPresent(lines, friend.getAddressLine2());
        addIfPresent(lines, join(friend.getPostcode(), friend.getCity()));
        return TextUtils.join("\n", lines);
    }

    private static void addIfPresent(List<String> lines, String value) {
        if (value != null && !value.trim().isEmpty()) {
            lines.add(value.trim());
        }
    }

    private static String join(String postcode, String city) {
        boolean hasPostcode = postcode != null && !postcode.trim().isEmpty();
        boolean hasCity = city != null && !city.trim().isEmpty();
        if (hasPostcode && hasCity) {
            return postcode.trim() + " " + city.trim();
        }
        if (hasPostcode) {
            return postcode.trim();
        }
        return hasCity ? city.trim() : "";
    }

    private String orNotProvided(String value) {
        return (value == null || value.trim().isEmpty())
                ? getString(R.string.not_provided) : value;
    }
}
