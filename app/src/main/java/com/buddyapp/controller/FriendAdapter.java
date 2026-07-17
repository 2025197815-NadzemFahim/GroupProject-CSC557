package com.buddyapp.controller;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buddyapp.R;
import com.buddyapp.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    public interface OnFriendClickListener {
        void onFriendClick(int friendId);
    }

    private List<Friend> friends = new ArrayList<>();
    private final OnFriendClickListener listener;

    public FriendAdapter(OnFriendClickListener listener) {
        this.listener = listener;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.name.setText(friend.getName());
        holder.subtitle.setText(buildSubtitle(friend));

        if (friend.getPhotoUri() != null) {
            holder.photo.setImageURI(Uri.parse(friend.getPhotoUri()));
        } else {
            holder.photo.setImageResource(R.drawable.ic_person);
        }

        holder.itemView.setOnClickListener(v -> listener.onFriendClick(friend.getFriendId()));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    private String buildSubtitle(Friend friend) {
        String state = friend.getStateName() != null ? friend.getStateName() : "";
        String phone = friend.getPhone();
        if (phone == null || phone.isEmpty()) {
            return state;
        }
        return state.isEmpty() ? phone : state + " • " + phone;
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        final ImageView photo;
        final TextView name;
        final TextView subtitle;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.friendPhoto);
            name = itemView.findViewById(R.id.friendName);
            subtitle = itemView.findViewById(R.id.friendSubtitle);
        }
    }
}
