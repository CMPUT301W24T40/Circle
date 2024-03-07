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

    private Event documentToEvent(DocumentSnapshot document) {
        String id = document.getId();
        String eventName = document.getString("Event Name");
        String location = document.getString("Location");
        String date = document.getString("Date");
        String time = document.getString("Time");
        String description = document.getString("Description");

        Log.d("PleaseWork", "Event Name: " + eventName);
        return new Event(id, eventName, location, date, time, description);
    }

    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> eventsList = new ArrayList<>();

        eventsRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Event event = documentToEvent(document);
                eventsList.add(event);
            }
        });

        Log.d("EventListLength", "Length of eventsList: " + eventsList.size());
        return eventsList;
    }

    public void addNewEvent(Event event) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Event ID", event.getID());
        data.put("Event Name", event.getEventName());
        data.put("Location", event.getLocation());
        data.put("Date", event.getDate());
        data.put("Time", event.getTime());
        data.put("Description", event.getDescription());

        eventsRef.document(event.getID()).set(data);
    }

    public void registerEvent(Attendee user, Event event) {
        DocumentReference eventDocRef = eventsRef.document(event.getID());
        CollectionReference userEventsRef = usersRef.document(user.getID()).collection("registeredEvents");

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            userEventsRef.add(documentSnapshot.getData());
        });
    }
}