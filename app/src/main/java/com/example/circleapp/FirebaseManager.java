package com.example.circleapp;

import android.content.Context;
import android.provider.Settings;
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
    private String phoneID;  // A reference to the phone ID of the user currently using the app

    /**
     * Constructs a new FirebaseManager instance. Contains all methods used to manage, query and
     * retrieve data from the Firestore database.
     */
    public FirebaseManager() {
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
     * Generates a random ID.
     *
     * @return The generated ID converted to a String
     */
    public String generateRandomID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sets the current phone ID.
     *
     * @param context   Context of the activity/fragment that is initiating this method.
     */
    public void setPhoneID(Context context) {
        this.phoneID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Retrieves the current phone ID.
     *
     * @return The ID of the current phone being used
     */
    public String getPhoneID() {
        return phoneID;
    }

    // Callback interfaces

    /**
     * Callback interface for Firestore operations with user documents.
     */
    public interface UserDocumentCallback {
        /**
         * Callback method to execute with the user document.
         *
         * @param exists Boolean representing whether the document exists in the database.
         */
        void onCallback(Boolean exists);
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

    /**
     * Callback interface for Firestore operations with a single event.
     */
    public interface EventCallback {
        /**
         * Callback method to execute with the event.
         *
         * @param event The event retrieved from Firestore
         */
        void onCallback(Event event);
    }

    // Methods for managing and retrieving user data

    /**
     * Searches Firestore database to check if a user exists for the current phone.
     *
     * @param callback The callback to execute with the boolean representing if the
     *                 user exists or not.
     */
    public void checkUserExists(UserDocumentCallback callback) {
        DocumentReference userDocRef = usersRef.document(phoneID);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                boolean exists = document.exists();
                callback.onCallback(exists);
            }
        });
    }

    /**
     * Searches Firestore database to check if a profile exists for the current user.
     *
     * @param callback The callback to execute with the boolean representing if the
     *                 profile exists or not.
     */
    public void checkProfileExists(UserDocumentCallback callback) {
        DocumentReference userDocRef = usersRef.document(phoneID);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String tempUserField = Objects.requireNonNull(document.get("HasProfile")).toString();
                    boolean tempUser = Boolean.parseBoolean(tempUserField);
                    callback.onCallback(tempUser);
                }
            }
        });
    }

    /**
     * Adds a new user to Firestore.
     *
     * @param user The user to add
     */
    public void addNewUser(Attendee user) {
        checkUserExists(exists -> {
            if (exists) {
                editUser(user);
            }
            else {
                HashMap<String, String> data = new HashMap<>();
                data.put("UserID", user.getID());
                data.put("FirstName", user.getFirstName());
                data.put("LastName", user.getLastName());
                data.put("Email", user.getEmail());
                data.put("Phone", user.getPhoneNumber());
                data.put("LocationEnabled", String.valueOf(user.isGeoEnabled()));
                data.put("HasProfile", String.valueOf(user.hasProfile()));
                data.put("Token", user.getToken());

                usersRef.document(phoneID).set(data);
                usersRef.document(phoneID).collection("registeredEvents");
                usersRef.document(phoneID).collection("createdEvents");
            }
        });
    }

    /**
     * Edits an existing user in Firestore.
     *
     * @param user The user to edit
     */
    public void editUser(Attendee user) {
        DocumentReference userDocRef = usersRef.document(phoneID);

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
                    updates.put("HasProfile", String.valueOf(user.hasProfile()));
                    updates.put("Token", user.getToken());

                    userDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully updated!"));
                }
            }
        });
    }

    /**
     * Retrieves registered users for the current event from Firestore.
     *
     * @param callback The callback to execute with the events list
     * @param eventID  The ID of the event we want to find the registered users for
     */
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

    public ArrayList<Attendee> getRegisteredUserTokens(String eventID) {
        ArrayList<Attendee> attendeesList = new ArrayList<>();

        eventsRef.document(eventID).collection("registeredUsers").get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Attendee attendee = document.toObject(Attendee.class);
                attendeesList.add(attendee);
            }
        });
        return attendeesList;
    }

    // Methods for managing and retrieving event data

    /**
     * Retrieves a specific event from Firestore.
     *
     * @param eventID The ID of the event to retrieve
     * @param callback The callback to execute with the event
     */
    public void getEvent(String eventID, EventCallback callback) {
        DocumentReference eventDocRef = eventsRef.document(eventID);

        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    callback.onCallback(event);
                }
            }
        });
    }

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

        usersRef.document(phoneID).collection("registeredEvents").get().addOnCompleteListener(task -> {
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

        usersRef.document(phoneID).collection("createdEvents").get().addOnCompleteListener(task -> {
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

        CollectionReference userEventsRef = usersRef.document(phoneID).collection("createdEvents");
        eventsRef.get().addOnSuccessListener(documentSnapshot -> userEventsRef.document(event.getID()).set(Objects.requireNonNull(data)));
    }

    /**
     * Registers an event for the current user in Firestore.
     *
     * @param event The event to register
     */
    public void registerEvent(Event event, Context context) {
        DocumentReference eventDocRef = eventsRef.document(event.getID());
        DocumentReference userDocRef = usersRef.document(phoneID);
        CollectionReference eventsCollectionRef = eventDocRef.collection("registeredUsers");
        CollectionReference userEventsRef = usersRef.document(phoneID).collection("registeredEvents");

        eventsCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int count = queryDocumentSnapshots.size();
            int capacity = Integer.parseInt(event.getCapacity());

            if (count < capacity || capacity == -1) {
                eventDocRef.get().addOnSuccessListener(documentSnapshot -> userEventsRef.document(event.getID()).set(Objects.requireNonNull(documentSnapshot.getData())));
                userDocRef.get().addOnSuccessListener(documentSnapshot -> eventsCollectionRef.document(phoneID).set(Objects.requireNonNull(documentSnapshot.getData())));
                Toast.makeText(context, "You've successfully registered for this event!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context, "This event has reached its maximum attendance. Sorry!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Checks in a user for a specific event.
     *
     * @param eventID The ID of the event to check in to.
     * @param userID The ID of the user to check in.
     */
    public void checkInEvent(String eventID, String userID){
        DocumentReference eventDocRef = eventsRef.document(eventID);
        CollectionReference checkedInUsersRef = eventDocRef.collection("checkedInUsers");

        HashMap<String, String> data = new HashMap<>();
        data.put("userID", userID);

        checkedInUsersRef.document(userID).set(data).addOnSuccessListener(aVoid ->
            System.out.println("User checked in successfully"))
            .addOnFailureListener(e ->
            System.err.println("Error checking in user: " + e.getMessage()));
    }
}