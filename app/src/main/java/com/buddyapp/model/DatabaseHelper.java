package com.buddyapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buddyapp.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "buddy.db";
    public static final int DB_VERSION = 1;

    public static final String T_USERS = "users";
    public static final String T_STATES = "states";
    public static final String T_FRIENDS = "friends";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USERS + " ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password_hash TEXT NOT NULL, "
                + "salt TEXT NOT NULL, "
                + "created_at INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + T_STATES + " ("
                + "state_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE)");

        db.execSQL("CREATE TABLE " + T_FRIENDS + " ("
                + "friend_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "gender TEXT NOT NULL, "
                + "phone TEXT, "
                + "email TEXT, "
                + "photo_uri TEXT, "
                + "address_line1 TEXT, "
                + "address_line2 TEXT, "
                + "postcode TEXT, "
                + "city TEXT, "
                + "state_id INTEGER, "
                + "created_at INTEGER NOT NULL, "
                + "updated_at INTEGER NOT NULL, "
                + "FOREIGN KEY(state_id) REFERENCES " + T_STATES + "(state_id))");

        seedStates(db);
    }

    private void seedStates(SQLiteDatabase db) {
        for (String name : Constants.MALAYSIAN_STATES) {
            ContentValues values = new ContentValues();
            values.put("name", name);
            db.insert(T_STATES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + T_STATES);
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }

    // ------------------------------------------------------------------ users

    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password_hash", user.getPasswordHash());
        values.put("salt", user.getSalt());
        values.put("created_at", System.currentTimeMillis());
        return db.insert(T_USERS, null, values);
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(T_USERS, null, "username = ?",
                new String[]{username}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password_hash")),
                    cursor.getString(cursor.getColumnIndexOrThrow("salt")));
        }
        cursor.close();
        return user;
    }

    public int getUserCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + T_USERS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ----------------------------------------------------------------- states

    public List<State> getAllStates() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(T_STATES, null, null, null, null, null, "name ASC");
        List<State> states = new ArrayList<>();
        while (cursor.moveToNext()) {
            states.add(new State(
                    cursor.getInt(cursor.getColumnIndexOrThrow("state_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))));
        }
        cursor.close();
        return states;
    }

    // ---------------------------------------------------------------- friends

    private ContentValues friendToValues(Friend friend) {
        ContentValues values = new ContentValues();
        values.put("name", friend.getName());
        values.put("gender", friend.getGender());
        values.put("phone", friend.getPhone());
        values.put("email", friend.getEmail());
        values.put("photo_uri", friend.getPhotoUri());
        values.put("address_line1", friend.getAddressLine1());
        values.put("address_line2", friend.getAddressLine2());
        values.put("postcode", friend.getPostcode());
        values.put("city", friend.getCity());
        if (friend.getStateId() > 0) {
            values.put("state_id", friend.getStateId());
        } else {
            values.putNull("state_id");
        }
        return values;
    }

    public long insertFriend(Friend friend) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = friendToValues(friend);
        long now = System.currentTimeMillis();
        values.put("created_at", now);
        values.put("updated_at", now);
        return db.insert(T_FRIENDS, null, values);
    }

    public int updateFriend(Friend friend) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = friendToValues(friend);
        values.put("updated_at", System.currentTimeMillis());
        return db.update(T_FRIENDS, values, "friend_id = ?",
                new String[]{String.valueOf(friend.getFriendId())});
    }

    public int deleteFriend(int friendId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(T_FRIENDS, "friend_id = ?",
                new String[]{String.valueOf(friendId)});
    }

    private static final String FRIEND_SELECT =
            "SELECT f.*, s.name AS state_name FROM " + T_FRIENDS + " f "
                    + "LEFT JOIN " + T_STATES + " s ON f.state_id = s.state_id ";

    private Friend cursorToFriend(Cursor cursor) {
        Friend friend = new Friend();
        friend.setFriendId(cursor.getInt(cursor.getColumnIndexOrThrow("friend_id")));
        friend.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        friend.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
        friend.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
        friend.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        friend.setPhotoUri(cursor.getString(cursor.getColumnIndexOrThrow("photo_uri")));
        friend.setAddressLine1(cursor.getString(cursor.getColumnIndexOrThrow("address_line1")));
        friend.setAddressLine2(cursor.getString(cursor.getColumnIndexOrThrow("address_line2")));
        friend.setPostcode(cursor.getString(cursor.getColumnIndexOrThrow("postcode")));
        friend.setCity(cursor.getString(cursor.getColumnIndexOrThrow("city")));
        int stateIdx = cursor.getColumnIndexOrThrow("state_id");
        friend.setStateId(cursor.isNull(stateIdx) ? 0 : cursor.getInt(stateIdx));
        friend.setStateName(cursor.getString(cursor.getColumnIndexOrThrow("state_name")));
        friend.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
        friend.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")));
        return friend;
    }

    public List<Friend> getAllFriends() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FRIEND_SELECT + "ORDER BY f.name COLLATE NOCASE", null);
        List<Friend> friends = new ArrayList<>();
        while (cursor.moveToNext()) {
            friends.add(cursorToFriend(cursor));
        }
        cursor.close();
        return friends;
    }

    public List<Friend> searchFriends(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String like = "%" + query + "%";
        Cursor cursor = db.rawQuery(FRIEND_SELECT
                + "WHERE f.name LIKE ? OR f.phone LIKE ? OR f.email LIKE ? "
                + "ORDER BY f.name COLLATE NOCASE", new String[]{like, like, like});
        List<Friend> friends = new ArrayList<>();
        while (cursor.moveToNext()) {
            friends.add(cursorToFriend(cursor));
        }
        cursor.close();
        return friends;
    }

    public Friend getFriendById(int friendId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(FRIEND_SELECT + "WHERE f.friend_id = ?",
                new String[]{String.valueOf(friendId)});
        Friend friend = null;
        if (cursor.moveToFirst()) {
            friend = cursorToFriend(cursor);
        }
        cursor.close();
        return friend;
    }

    // ----------------------------------------------------------------- reports

    public Map<String, Integer> getGenderCounts() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT gender, COUNT(*) FROM " + T_FRIENDS + " GROUP BY gender", null);
        Map<String, Integer> counts = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            counts.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return counts;
    }

    public Map<String, Integer> getStateCounts() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT s.name, COUNT(f.friend_id) FROM " + T_STATES + " s "
                        + "JOIN " + T_FRIENDS + " f ON f.state_id = s.state_id "
                        + "GROUP BY s.state_id ORDER BY s.name", null);
        Map<String, Integer> counts = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            counts.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return counts;
    }

    public String buildFriendSummary(){
        List<Friend> friends = getAllFriends();

        if(friends.isEmpty()){
            return "The user has no friends saved yet.";
        }

        StringBuilder sb = new StringBuilder();

        for (Friend f : friends) {
            sb.append("- ").append(f.getName())
                    .append(" | gender: ").append(f.getGender())
                    .append(" | state: ")
                    .append(f.getStateName() == null ? "unknown" : f.getStateName())
                    .append("\n");
        }

        return sb.toString();
    }
}
