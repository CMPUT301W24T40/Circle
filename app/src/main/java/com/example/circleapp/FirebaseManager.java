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
                    String tempUserField = Objects.requireNonNull(document.get("hasProfile")).toString();
                    boolean tempUser = Boolean.parseBoolean(tempUserField);
                    callback.onCallback(tempUser);
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
                data.put("ID", user.getID());
                data.put("firstName", user.getFirstName());
                data.put("lastName", user.getLastName());
                data.put("email", user.getEmail());
                data.put("phoneNumber", user.getPhoneNumber());
                data.put("isGeoEnabled", String.valueOf(user.isGeoEnabled()));
                data.put("profilePic", String.valueOf(user.getProfilePic()));
                data.put("homepage", String.valueOf(user.getHomepage()));
                data.put("hasProfile", String.valueOf(user.hasProfile()));
                data.put("token", user.getToken());

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
                    updates.put("firstName", user.getFirstName());
                    updates.put("lastName", user.getLastName());
                    updates.put("email", user.getEmail());
                    updates.put("phoneNumber", user.getPhoneNumber());
                    updates.put("isGeoEnabled", String.valueOf(user.isGeoEnabled()));
                    updates.put("profilePic", String.valueOf(user.getProfilePic()));
                    updates.put("homepage", String.valueOf(user.getHomepage()));
                    updates.put("hasProfile", String.valueOf(user.hasProfile()));
                    updates.put("token", user.getToken());

                    userDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully updated!"));
                }
            }
        });
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
     * Retrieves an event by its check-in ID.
     *
     * @param checkinID The check-in ID of the event to retrieve
     * @param callback The callback to execute with the event
     */

    public void getEventByCheckInID(String checkinID, EventCallback callback) {
        eventsRef.whereEqualTo("checkInID", checkinID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().getDocuments().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    if (document.exists()) {
                        Event event = document.toObject(Event.class);
                        callback.onCallback(event);
                    } else {
                        callback.onCallback(null);
                    }
                } else {
                    Log.d("Firestore", "No documents found with the provided check-in ID");
                }
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
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
        data.put("eventPosterURL", event.getEventPosterURL());
        data.put("checkInID", event.getCheckInID());

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
     */
    public void checkInEvent(String eventID){
        DocumentReference eventDocRef = eventsRef.document(eventID);
        DocumentReference userDocRef = usersRef.document(phoneID);
        CollectionReference checkedInUsersRef = eventDocRef.collection("checkedInUsers");
        CollectionReference userEventsRef = usersRef.document(phoneID).collection("checkedInEvents");


        HashMap<String, String> data = new HashMap<>();
        data.put("ID", phoneID);

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> userEventsRef.document(eventID).set(Objects.requireNonNull(documentSnapshot.getData())));
        userDocRef.get().addOnSuccessListener(documentSnapshot -> checkedInUsersRef.document(phoneID).set(Objects.requireNonNull(documentSnapshot.getData())));
    }

    /**
     * Edits an existing event in Firestore.
     *
     * @param event The event to edit
     */
    public void editEvent(Event event) {
        DocumentReference eventDocRef = eventsRef.document(event.getID());

        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("eventName", event.getEventName());
                    updates.put("location", event.getLocation());
                    updates.put("date", event.getDate());
                    updates.put("time", event.getTime());
                    updates.put("description", event.getDescription());
                    updates.put("capacity", event.getCapacity());
                    updates.put("eventPosterURL", event.getEventPosterURL());
                    updates.put("checkInID", event.getCheckInID());

                    eventDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event successfully updated!"))
                            .addOnFailureListener(e -> Log.d("Firestore", "Error updating event!", e));
                }
            }
        });
    }
}