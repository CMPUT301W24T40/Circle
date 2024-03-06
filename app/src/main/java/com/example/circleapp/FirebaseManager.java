package com.example.circleapp;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class FirebaseManager {

    private static FirebaseManager instance;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;

    private FirebaseManager() {
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        // Initialize CollectionReference for users
        usersRef = db.collection("users");
        eventsRef = db.collection("events");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public String generateRandomUserId() {
        return UUID.randomUUID().toString();
    }

    public void addNewUser(Attendee user) {
        // Add the user to the Firestore collection
        HashMap<String, String> data = new HashMap<>();
        data.put("User ID", user.getID());
        data.put("First Name", user.getFirstName());
        data.put("Last Name", user.getLastName());
        data.put("Email", user.getEmail());
        data.put("Phone Number", user.getPhoneNumber());
        data.put("Location Enabled", String.valueOf(user.isGeoEnabled()));
        //data.put("Profile Picture", user.getProfilePic().toString());

        usersRef
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
    }
}