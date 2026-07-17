package com.buddyapp.model;

import androidx.annotation.NonNull;

public class State {

    private final int stateId;
    private final String name;

    public State(int stateId, String name) {
        this.stateId = stateId;
        this.name = name;
    }

    public int getStateId() {
        return stateId;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
