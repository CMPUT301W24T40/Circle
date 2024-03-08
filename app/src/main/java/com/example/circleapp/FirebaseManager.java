package com.example.circleapp;

import android.util.Log;

import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.BaseObjects.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final CollectionReference usersRef;
    private final CollectionReference eventsRef;
    private String currentUserID;

    private FirebaseManager() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");
        eventsRef = db.collection("events");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void setCurrentUserID(String ID) { this.currentUserID = ID; }

    public String generateRandomID() {
        return UUID.randomUUID().toString();
    }

    public void addNewUser(Attendee user) {
        HashMap<String, String> data = new HashMap<>();
        data.put("UserID", user.getID());
        data.put("FirstName", user.getFirstName());
        data.put("LastName", user.getLastName());
        data.put("Email", user.getEmail());
        data.put("Phone", user.getPhoneNumber());
        data.put("LocationEnabled", String.valueOf(user.isGeoEnabled()));
        //data.put("Profile Picture", user.getProfilePic().toString());

        usersRef.document(user.getID()).set(data);
        usersRef.document(user.getID()).collection("registeredEvents");
    }

    public void editUser(Attendee user) {
        DocumentReference userDocRef = usersRef.document(user.getID());

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("FirstName", user.getFirstName());
                    updates.put("LastName", user.getLastName());
                    updates.put("Email", user.getEmail());
                    updates.put("Phone", user.getPhoneNumber());
                    updates.put("LocationEnabled", String.valueOf(user.isGeoEnabled()));

                    userDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully updated!"));
                }
            }
        });
    }

    public void getAllEvents(FirestoreCallback callback) {
        ArrayList<Event> eventsList = new ArrayList<>();

        eventsRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                eventsList.add(event);
            }
            callback.onCallback(eventsList);
        });
    }

    public void getRegisteredEvents(FirestoreCallback callback) {
        ArrayList<Event> eventsList = new ArrayList<>();

        usersRef.document(currentUserID).collection("registeredEvents").get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                eventsList.add(event);
            }
            callback.onCallback(eventsList);
        });
    }

    public void addNewEvent(Event event) {
        HashMap<String, String> data = new HashMap<>();
        data.put("ID", event.getID());
        data.put("eventName", event.getEventName());
        data.put("location", event.getLocation());
        data.put("date", event.getDate());
        data.put("time", event.getTime());
        data.put("description", event.getDescription());

        eventsRef.document(event.getID()).set(data);
    }

    public void registerEvent(Event event) {
        DocumentReference eventDocRef = eventsRef.document(event.getID());
        CollectionReference userEventsRef = usersRef.document(currentUserID).collection("registeredEvents");

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> userEventsRef.add(Objects.requireNonNull(documentSnapshot.getData())));
    }

    public interface FirestoreCallback {
        void onCallback(ArrayList<Event> eventsList);
    }
}