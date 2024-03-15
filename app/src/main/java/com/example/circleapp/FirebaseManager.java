package com.example.circleapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Manages interactions with Firebase Firestore.
 */
public class FirebaseManager {
    private static FirebaseManager instance;
    private final CollectionReference usersRef; // A reference to the "users" collection in Firestore
    private final CollectionReference eventsRef; // A reference to the "events" collection in Firestore
    private String currentUserID; // A reference to the user ID of the user currently using the app

    /**
     * Constructs a new FirebaseManager instance. Contains all methods used to manage, query and
     * retrieve data from the Firestore database.
     */
    private FirebaseManager() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");
        eventsRef = db.collection("events");
    }

    /**
     * Gets the singleton instance of FirebaseManager.
     *
     * @return The FirebaseManager instance
     */
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    /**
     * Sets the current user ID.
     *
     * @param ID The ID of the current user
     */
    public void setCurrentUserID(String ID) {
        this.currentUserID = ID;
    }

    /**
     * Retrieves the current user ID.
     *
     * @return The ID of the current user
     */
    public String getCurrentUserID() { return currentUserID; }

    /**
     * Generates a random ID.
     *
     * @return The generated ID converted to a String
     */
    public String generateRandomID() {
        return UUID.randomUUID().toString();
    }

    // Methods for managing and retrieving user data

    /**
     * Adds a new user to Firestore.
     *
     * @param user The user to add
     */
    public void addNewUser(Attendee user) {
        HashMap<String, String> data = new HashMap<>();
        data.put("UserID", user.getID());
        data.put("FirstName", user.getFirstName());
        data.put("LastName", user.getLastName());
        data.put("Email", user.getEmail());
        data.put("Phone", user.getPhoneNumber());
        data.put("LocationEnabled", String.valueOf(user.isGeoEnabled()));

        usersRef.document(user.getID()).set(data);
        usersRef.document(user.getID()).collection("registeredEvents");
        usersRef.document(user.getID()).collection("createdEvents");
    }

    /**
     * Edits an existing user in Firestore.
     *
     * @param user The user to edit
     */
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

    public void getRegisteredUsers(AttendeesCallback callback, String eventID) {
        ArrayList<Attendee> attendeesList = new ArrayList<>();

        eventsRef.document(eventID).collection("registeredUsers").get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Attendee attendee = document.toObject(Attendee.class);
                attendeesList.add(attendee);
            }
            callback.onCallback(attendeesList);
        });
    }

    /**
     * Callback interface for Firestore operations with attendees.
     */
    public interface AttendeesCallback {
        /**
         * Callback method to execute with the attendees list.
         *
         * @param attendeesList The list of attendees retrieved from Firestore
         */
        void onCallback(ArrayList<Attendee> attendeesList);
    }

    // Methods for managing and retrieving event data

    /**
     * Retrieves all events from Firestore.
     *
     * @param callback The callback to execute with the events list
     */
    public void getAllEvents(EventsCallback callback) {
        ArrayList<Event> eventsList = new ArrayList<>();

        eventsRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                eventsList.add(event);
            }
            callback.onCallback(eventsList);
        });
    }

    /**
     * Retrieves registered events for the current user from Firestore.
     *
     * @param callback The callback to execute with the events list
     */
    public void getRegisteredEvents(EventsCallback callback) {
        ArrayList<Event> eventsList = new ArrayList<>();

        usersRef.document(currentUserID).collection("registeredEvents").get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                eventsList.add(event);
            }
            callback.onCallback(eventsList);
        });
    }

    /**
     * Retrieves created events for the current user from Firestore.
     *
     * @param callback The callback to execute with the events list
     */
    public void getCreatedEvents (EventsCallback callback) {
        ArrayList<Event> eventsList = new ArrayList<>();

        usersRef.document(currentUserID).collection("createdEvents").get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Event event = document.toObject(Event.class);
                eventsList.add(event);
            }
            callback.onCallback(eventsList);
        });
    }

    /**
     * Adds a new event to Firestore.
     *
     * @param event The event to add
     */
    public void createEvent(Event event) {
        HashMap<String, String> data = new HashMap<>();
        data.put("ID", event.getID());
        data.put("eventName", event.getEventName());
        data.put("location", event.getLocation());
        data.put("date", event.getDate());
        data.put("time", event.getTime());
        data.put("description", event.getDescription());
        data.put("capacity", event.getCapacity());

        eventsRef.document(event.getID()).set(data);
        eventsRef.document(event.getID()).collection("registeredUsers");

        CollectionReference userEventsRef = usersRef.document(currentUserID).collection("createdEvents");
        eventsRef.get().addOnSuccessListener(documentSnapshot -> userEventsRef.document(event.getID()).set(Objects.requireNonNull(data)));
    }

    /**
     * Registers an event for the current user in Firestore.
     *
     * @param event The event to register
     */
    public void registerEvent(Event event, Context context) {
        DocumentReference eventDocRef = eventsRef.document(event.getID());
        DocumentReference userDocRef = usersRef.document(currentUserID);
        CollectionReference eventsCollectionRef = eventDocRef.collection("registeredUsers");
        CollectionReference userEventsRef = usersRef.document(currentUserID).collection("registeredEvents");

        eventsCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int count = queryDocumentSnapshots.size();
            int capacity = Integer.parseInt(event.getCapacity());

            if (count < capacity || capacity == -1) {
                eventDocRef.get().addOnSuccessListener(documentSnapshot -> userEventsRef.document(event.getID()).set(Objects.requireNonNull(documentSnapshot.getData())));
                userDocRef.get().addOnSuccessListener(documentSnapshot -> eventsCollectionRef.document(currentUserID).set(Objects.requireNonNull(documentSnapshot.getData())));
                Toast.makeText(context, "You've successfully registered for this event!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context, "This event has reached its maximum attendance. Sorry!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Callback interface for Firestore operations with events.
     */
    public interface EventsCallback {
        /**
         * Callback method to execute with the events list.
         *
         * @param eventsList The list of events retrieved from Firestore
         */
        void onCallback(ArrayList<Event> eventsList);
    }
}