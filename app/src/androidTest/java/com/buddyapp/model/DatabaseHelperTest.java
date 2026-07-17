package com.buddyapp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.buddyapp.util.PasswordHasher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

/**
 * Instrumented tests for {@link DatabaseHelper} against a real SQLite database.
 * A throwaway database file is used and deleted around each test.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private Context context;
    private DatabaseHelper db;
    private int stateId;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(DatabaseHelper.DB_NAME);
        db = new DatabaseHelper(context);
        List<State> states = db.getAllStates();
        stateId = states.get(0).getStateId();
    }

    @After
    public void tearDown() {
        db.close();
        context.deleteDatabase(DatabaseHelper.DB_NAME);
    }

    private Friend sample(String name, String gender) {
        Friend friend = new Friend();
        friend.setName(name);
        friend.setGender(gender);
        friend.setPhone("0123456789");
        friend.setEmail(name.toLowerCase() + "@mail.com");
        friend.setStateId(stateId);
        return friend;
    }

    @Test
    public void seedsSixteenStates() {
        assertEquals(16, db.getAllStates().size());
    }

    @Test
    public void insertThenReadJoinsState() {
        long id = db.insertFriend(sample("Ali", "Male"));
        assertTrue(id > 0);

        List<Friend> all = db.getAllFriends();
        assertEquals(1, all.size());
        assertEquals("Ali", all.get(0).getName());
        assertNotNull(all.get(0).getStateName());
    }

    @Test
    public void updateChangesTheRow() {
        long id = db.insertFriend(sample("Ali", "Male"));
        Friend friend = db.getFriendById((int) id);
        friend.setName("Ali Updated");

        assertEquals(1, db.updateFriend(friend));
        assertEquals("Ali Updated", db.getFriendById((int) id).getName());
    }

    @Test
    public void deleteRemovesTheRow() {
        long id = db.insertFriend(sample("Ali", "Male"));
        assertEquals(1, db.deleteFriend((int) id));
        assertTrue(db.getAllFriends().isEmpty());
    }

    @Test
    public void searchMatchesByName() {
        db.insertFriend(sample("Alice", "Female"));
        db.insertFriend(sample("Bob", "Male"));
        assertEquals(1, db.searchFriends("ali").size());
    }

    @Test
    public void genderCountsGroupFriends() {
        db.insertFriend(sample("Ali", "Male"));
        db.insertFriend(sample("Siti", "Female"));
        Map<String, Integer> counts = db.getGenderCounts();
        assertEquals(2, counts.size());
    }

    @Test
    public void userInsertLookupAndVerify() {
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash("secret123", salt);
        db.insertUser(new User("owner", hash, salt));

        User user = db.getUserByUsername("owner");
        assertNotNull(user);
        assertTrue(PasswordHasher.verify("secret123", user.getSalt(), user.getPasswordHash()));
        assertEquals(1, db.getUserCount());
    }
}
