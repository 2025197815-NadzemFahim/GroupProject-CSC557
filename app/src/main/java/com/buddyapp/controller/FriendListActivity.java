package com.buddyapp.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buddyapp.R;
import com.buddyapp.model.DatabaseHelper;
import com.buddyapp.model.Friend;
import com.buddyapp.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private FriendAdapter adapter;

    private RecyclerView recyclerView;
    private View emptyView;
    private TextView emptyTitle;
    private TextView emptyHint;

    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        emptyTitle = findViewById(R.id.emptyTitle);
        emptyHint = findViewById(R.id.emptyHint);

        adapter = new FriendAdapter(this::openDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addFab).setOnClickListener(v -> openAddEdit(com.buddyapp.util.Constants.NO_ID));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        setUpSearch(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadFriends();
    }

    private void reloadFriends() {
        List<Friend> friends = query.isEmpty()
                ? db.getAllFriends()
                : db.searchFriends(query);
        adapter.setFriends(friends);

        boolean empty = friends.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) {
            boolean searching = !query.isEmpty();
            emptyTitle.setText(searching ? R.string.empty_search_title : R.string.empty_friends_title);
            emptyHint.setText(searching ? R.string.empty_search_hint : R.string.empty_friends_hint);
        }
    }

    private void setUpSearch(MaterialToolbar toolbar) {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText == null ? "" : newText.trim();
                reloadFriends();
                return true;
            }
        });
    }

    private boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reports) {
            startActivity(new Intent(this, ReportActivity.class));
            return true;
        }
        if (id == R.id.action_logout) {
            session.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_ask_ai) {
            startActivity(new Intent(this, ChatActivity.class));
            return true;
        }
        return false;
    }

    private void openAddEdit(int friendId) {
        Intent intent = new Intent(this, AddEditFriendActivity.class);
        intent.putExtra(com.buddyapp.util.Constants.EXTRA_FRIEND_ID, friendId);
        startActivity(intent);
    }

    private void openDetail(int friendId) {
        Intent intent = new Intent(this, FriendDetailActivity.class);
        intent.putExtra(com.buddyapp.util.Constants.EXTRA_FRIEND_ID, friendId);
        startActivity(intent);
    }
}
