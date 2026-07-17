package com.buddyapp.controller;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buddyapp.R;
import com.buddyapp.model.ChatMessage;
import com.buddyapp.model.DatabaseHelper;
import com.buddyapp.model.GeminiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ChatActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private GeminiClient gemini;
    private ChatAdapter adapter;
    private RecyclerView recycler;
    private EditText messageInput;
    private MaterialButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = new DatabaseHelper(this);
        gemini = new GeminiClient();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recycler = findViewById(R.id.chatRecycler);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        adapter = new ChatAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.add(new ChatMessage(getString(R.string.chat_greeting), false));

        sendButton.setOnClickListener(v -> send());
    }

    private void send() {
        String question = messageInput.getText().toString().trim();
        if (question.isEmpty()) {
            return;
        }

        adapter.add(new ChatMessage(question, true));   // show your message
        messageInput.setText("");
        scrollToBottom();
        sendButton.setEnabled(false);

        String summary = db.buildFriendSummary();
        String prompt = buildPrompt(summary, question);

        android.util.Log.d("GEMINI_PROMPT", "Prompt Length: " + prompt.length() + " chars\n" + prompt);

        gemini.ask(prompt, new GeminiClient.Callback() {
            @Override
            public void onSuccess(String text) {
                adapter.add(new ChatMessage(text, false));
                scrollToBottom();
                sendButton.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                adapter.add(new ChatMessage("Error: " + message, false));
                scrollToBottom();
                sendButton.setEnabled(true);
            }
        });
    }

    private String buildPrompt(String summary, String question) {
        return "You are a helpful assistant inside a contacts app. "
                + "Answer the user's question using ONLY the friend data below. "
                + "If the answer is not in the data, say you don't have that information. "
                + "Keep answers short and friendly.\n\n"
                + "Friends:\n" + summary + "\n"
                + "Question: " + question;
    }

    private void scrollToBottom() {
        recycler.scrollToPosition(adapter.getItemCount() - 1);
    }
}