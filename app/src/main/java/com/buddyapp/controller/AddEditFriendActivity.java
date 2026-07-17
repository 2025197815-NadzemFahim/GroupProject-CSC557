package com.buddyapp.controller;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.buddyapp.R;
import com.buddyapp.model.DatabaseHelper;
import com.buddyapp.model.Friend;
import com.buddyapp.model.State;
import com.buddyapp.util.Constants;
import com.buddyapp.util.ImageStorage;
import com.buddyapp.util.Validators;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AddEditFriendActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ActivityResultLauncher<String> imagePicker;

    private int friendId = Constants.NO_ID;
    private long createdAt;
    private String existingPhotoUri;
    private Uri pickedImage;

    private final List<State> states = new ArrayList<>();

    private ImageView friendPhoto;
    private EditText nameInput;
    private EditText phoneInput;
    private EditText emailInput;
    private EditText address1Input;
    private EditText address2Input;
    private EditText postcodeInput;
    private EditText cityInput;
    private RadioGroup genderGroup;
    private Spinner stateSpinner;
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_friend);

        db = new DatabaseHelper(this);

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        pickedImage = uri;
                        friendPhoto.setImageURI(uri);
                    }
                });

        bindViews();
        loadStateSpinner();

        friendId = getIntent().getIntExtra(Constants.EXTRA_FRIEND_ID, Constants.NO_ID);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        boolean editing = friendId != Constants.NO_ID;
        toolbar.setTitle(editing ? R.string.title_edit_friend : R.string.title_add_friend);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        if (editing) {
            populate(db.getFriendById(friendId));
        }
    }

    private void bindViews() {
        friendPhoto = findViewById(R.id.friendPhoto);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        address1Input = findViewById(R.id.address1Input);
        address2Input = findViewById(R.id.address2Input);
        postcodeInput = findViewById(R.id.postcodeInput);
        cityInput = findViewById(R.id.cityInput);
        genderGroup = findViewById(R.id.genderGroup);
        stateSpinner = findViewById(R.id.stateSpinner);
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        findViewById(R.id.selectPhotoButton).setOnClickListener(v -> imagePicker.launch("image/*"));
    }

    private void loadStateSpinner() {
        states.clear();
        states.addAll(db.getAllStates());
        ArrayAdapter<State> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);
    }

    private void populate(Friend friend) {
        if (friend == null) {
            return;
        }
        nameInput.setText(friend.getName());
        phoneInput.setText(friend.getPhone());
        emailInput.setText(friend.getEmail());
        address1Input.setText(friend.getAddressLine1());
        address2Input.setText(friend.getAddressLine2());
        postcodeInput.setText(friend.getPostcode());
        cityInput.setText(friend.getCity());
        ((android.widget.RadioButton) findViewById(R.id.radioMale))
                .setChecked("Male".equals(friend.getGender()));
        ((android.widget.RadioButton) findViewById(R.id.radioFemale))
                .setChecked("Female".equals(friend.getGender()));

        existingPhotoUri = friend.getPhotoUri();
        createdAt = friend.getCreatedAt();
        if (existingPhotoUri != null) {
            friendPhoto.setImageURI(Uri.parse(existingPhotoUri));
        }

        for (int i = 0; i < states.size(); i++) {
            if (states.get(i).getStateId() == friend.getStateId()) {
                stateSpinner.setSelection(i);
                break;
            }
        }
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            save();
            return true;
        }
        return false;
    }

    private void save() {
        String name = valueOf(nameInput);
        if (!Validators.isNotBlank(name)) {
            nameLayout.setError(getString(R.string.error_name_required));
            return;
        }
        nameLayout.setError(null);

        int checkedId = genderGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            toast(getString(R.string.error_gender_required));
            return;
        }
        String gender = checkedId == R.id.radioMale ? "Male" : "Female";

        String email = valueOf(emailInput);
        if (!Validators.isValidEmail(email)) {
            emailLayout.setError(getString(R.string.error_email_invalid));
            return;
        }
        emailLayout.setError(null);

        if (stateSpinner.getSelectedItem() == null) {
            toast(getString(R.string.error_states_loading));
            return;
        }
        int stateId = ((State) stateSpinner.getSelectedItem()).getStateId();

        // Copy a newly picked photo into internal storage; otherwise keep the old one.
        String photoUri = existingPhotoUri;
        if (pickedImage != null) {
            String copied = ImageStorage.copyToInternal(this, pickedImage);
            if (copied != null) {
                photoUri = copied;
            }
        }

        Friend friend = new Friend();
        friend.setName(name);
        friend.setGender(gender);
        friend.setPhone(emptyToNull(valueOf(phoneInput)));
        friend.setEmail(emptyToNull(email));
        friend.setPhotoUri(photoUri);
        friend.setAddressLine1(emptyToNull(valueOf(address1Input)));
        friend.setAddressLine2(emptyToNull(valueOf(address2Input)));
        friend.setPostcode(emptyToNull(valueOf(postcodeInput)));
        friend.setCity(emptyToNull(valueOf(cityInput)));
        friend.setStateId(stateId);

        if (friendId != Constants.NO_ID) {
            friend.setFriendId(friendId);
            friend.setCreatedAt(createdAt);
            db.updateFriend(friend);
        } else {
            db.insertFriend(friend);
        }

        toast(getString(R.string.friend_saved));
        finish();
    }

    private static String valueOf(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
