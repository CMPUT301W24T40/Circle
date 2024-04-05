package com.example.circleapp.Firebase;

import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.circleapp.BaseObjects.Announcement;
import com.example.circleapp.BaseObjects.Attendee;
import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.UserDisplay.CheckedInAttendeesCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Manages interactions with Firebase Firestore.
 */
public class FirebaseManager {
    private static FirebaseManager instance;
    private final CollectionReference usersRef;
    private final CollectionReference eventsRef;
    private final CollectionReference adminsRef;
    private String phoneID;
    final double NULL_DOUBLE = -999999999;

    /**
     * Constructs a new FirebaseManager instance. Contains all methods used to manage, query and
     * retrieve data from the Firestore database.
     */
    public FirebaseManager() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");
        eventsRef = db.collection("events");
        adminsRef = db.collection("admins");
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

    /**
     * Callback interface for Firestore operations with URLs.
     */
    public interface URLCallback {
        /**
         * Callback method to execute with the URL list.
         *
         * @param urlList The list of URLS retrieved from Firestore
         */
        void onCallback(ArrayList<String> urlList);
    }

    /**
     * Callback interface for Firestore operations with an event's check-in count.
     */
    public interface CheckInCountCallback {
        /**
         * Callback method to execute with the count.
         *
         * @param data The # of people checked in for the event, retrieved from Firestore
         */
        void onCallback(int data);
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
    public void checkProfileExists(String ID, UserDocumentCallback callback) {
        DocumentReference userDocRef = usersRef.document(ID);
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
     * Retrieves all users of the app from Firestore.
     *
     * @param callback The callback to execute with the events list
     */
    public void getAllUsers(AttendeesCallback callback) {
        ArrayList<Attendee> attendeesList = new ArrayList<>();

        usersRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                Attendee attendee = document.toObject(Attendee.class);
                attendeesList.add(attendee);
            }
            callback.onCallback(attendeesList);
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

    /**
     * Retrieves registered users tokens for the current event from Firestore.
     *
     * @param eventID  The ID of the event we want to find the registered user tokens for
     */
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
     * Retrieves checked-in users for the current event from Firestore.
     *
     * @param eventID  The ID of the event we want to find the checked-in users for
     */
    public void getCheckedInAttendees(String eventID, CheckedInAttendeesCallback callback) {
        ArrayList<Attendee> checkedInAttendeesList = new ArrayList<>();

        eventsRef.document(eventID).collection("checkedInUsers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Attendee attendee = document.toObject(Attendee.class);
                    checkedInAttendeesList.add(attendee);
                }
                callback.onAttendeesReceived(checkedInAttendeesList);
            }
        });
    }

    /**
     * Retrieves check-in count for the current event and user from Firestore.
     *
     * @param eventID  The ID of the event we want to find the checked-in users for
     */
    public void getCheckInCount(String eventID, String userID, final CheckInCountCallback callback) {
        DocumentReference eventDocRef = eventsRef.document(eventID);
        CollectionReference checkInsCollection = eventDocRef.collection("checkIns");

        checkInsCollection.whereEqualTo("userID", userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = 0;
                if (task.getResult() != null) {
                    count = task.getResult().size();
                }
                callback.onCallback(count);
                Log.d("CheckInCount", "Check-in count for user " + userID + " is " + count);

            } else {
                Log.d("FirebaseManager", "Error getting check-in count: ", task.getException());
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
                editUser(user, null);
            }
            else {
                HashMap<String, Object> data = new HashMap<>();
                data.put("ID", user.getID());
                data.put("firstName", user.getFirstName());
                data.put("lastName", user.getLastName());
                data.put("email", user.getEmail());
                data.put("phoneNumber", user.getPhoneNumber());
                data.put("profilePic", String.valueOf(user.getProfilePic()));
                data.put("homepage", String.valueOf(user.getHomepage()));
                data.put("hasProfile", String.valueOf(user.hasProfile()));
                data.put("token", user.getToken());
                data.put("locationLatitude", user.getLocationLatitude());
                data.put("locationLongitude", user.getLocationLongitude());

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
    public void editUser(Attendee user, @Nullable Runnable onComplete) {
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
                    updates.put("profilePic", String.valueOf(user.getProfilePic()));
                    updates.put("homepage", String.valueOf(user.getHomepage()));
                    updates.put("hasProfile", String.valueOf(user.hasProfile()));
                    updates.put("token", user.getToken());
                    if ((user.getLocationLatitude() == NULL_DOUBLE)
                            && (user.getLocationLongitude() == NULL_DOUBLE)) {
                        updates.put("locationLatitude", null);
                        updates.put("locationLongitude", null);
                    } else {
                        updates.put("locationLatitude", user.getLocationLatitude());
                        updates.put("locationLongitude", user.getLocationLongitude());
                    }

                    if (onComplete == null) {
                        userDocRef.update(updates)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully updated!"));
                        updateRegisteredUser(updates);
                    } else {
                        userDocRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Document successfully updated!");
                                    onComplete.run();
                                });
                    }
                }
            }
        });
    }

    /**
     * When a user edits their profile, this function is used to
     * update their information for the events they're registered in.
     *
     * @param updates The updated information passed by editUser()
     */
    public void updateRegisteredUser(Map<String, Object> updates) {
        getRegisteredEvents(eventsList -> {
            for (Event event : eventsList) {
                DocumentReference eventDocRef = eventsRef.document(event.getID());
                CollectionReference registeredUsersCollection = eventDocRef.collection("registeredUsers");

                registeredUsersCollection.get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(phoneID)) {
                            DocumentReference userDocRef = registeredUsersCollection.document(phoneID);

                            userDocRef.update(updates);
                        }
                    }
                });
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
     * Checks if a user is registered for a specific event.
     *
     * @param userID The ID of the user to check registration for.
     * @param eventID The ID of the event to check registration for.
     */
    public void isUserRegistered(String eventID, String userID, UserDocumentCallback callback) {
        DocumentReference eventDocRef = eventsRef.document(eventID);
        CollectionReference registeredUsersCollection = eventDocRef.collection("registeredUsers");

        registeredUsersCollection.document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                boolean exists = document.exists();
                callback.onCallback(exists);
            }
        });
    }

    /**
     * Unregisters a user from a specific event.
     *
     * @param context The context of the activity/fragment that is initiating this method.
     * @param event The event to unregister from
     */
    public void unregisterEvent(Event event, Context context) {
        DocumentReference eventDocRef = eventsRef.document(event.getID());
        CollectionReference registeredUsersCollection = eventDocRef.collection("registeredUsers");
        DocumentReference userEventDocRef = usersRef.document(phoneID).collection("registeredEvents").document(event.getID());

        registeredUsersCollection.document(phoneID).delete()
                .addOnSuccessListener(aVoid -> userEventDocRef.delete()
                        .addOnSuccessListener(aVoid2 -> Toast.makeText(context, "You've successfully unregistered from this event!", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to unregister from this event!", Toast.LENGTH_LONG).show()))
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to unregister from this event!", Toast.LENGTH_LONG).show());
    }

    /**
     * Checks in a user for a specific event.
     *
     * @param eventID The ID of the event to check in to.
     */
    public void checkInEvent(String eventID, @Nullable Location location){
        DocumentReference eventDocRef = eventsRef.document(eventID);
        DocumentReference userDocRef = usersRef.document(phoneID);
        DocumentReference checkInsRef = FirebaseFirestore.getInstance().collection("events").document(eventID).collection("checkIns").document();
        CollectionReference checkedInUsersRef = eventDocRef.collection("checkedInUsers");
        CollectionReference userEventsRef = usersRef.document(phoneID).collection("checkedInEvents");

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Attendee attendee = document.toObject(Attendee.class);
                    if (location != null) {
                        assert attendee != null;
                        attendee.setLocationLatitude(location.getLatitude());
                        attendee.setLocationLongitude(location.getLongitude());
                    }
                    else {
                        assert attendee != null;
                        attendee.setLocationLatitude(NULL_DOUBLE);
                        attendee.setLocationLongitude(NULL_DOUBLE);
                    }
                    editUser(attendee, () -> userDocRef.get().addOnSuccessListener(updatedDocument -> checkedInUsersRef.document(phoneID).set(updatedDocument.getData())
                            .addOnSuccessListener(aVoid -> performCheckIn(eventDocRef, userEventsRef, checkInsRef, location))));
                }
            }
        });
    }

    private void performCheckIn(DocumentReference eventDocRef, CollectionReference userEventsRef, DocumentReference checkInsRef, @Nullable Location location) {
        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> eventData = documentSnapshot.getData();
                if (eventData != null) {
                    userEventsRef.document(eventDocRef.getId()).set(eventData)
                            .addOnSuccessListener(aVoid -> {
                                Map<String, Object> checkInData = new HashMap<>();
                                checkInData.put("userID", phoneID);
                                checkInData.put("timestamp", FieldValue.serverTimestamp());
                                if (location != null) {
                                    checkInData.put("locationLatitude", location.getLatitude());
                                    checkInData.put("locationLongitude", location.getLongitude());
                                } else {
                                    checkInData.put("locationLatitude", null);
                                    checkInData.put("locationLongitude", null);
                                }

                                checkInsRef.set(checkInData)
                                        .addOnSuccessListener(aVoid1 -> Log.d("Check-in", "Check-in successful"))
                                        .addOnFailureListener(e -> Log.w("Check-in", "Error checking in", e));
                            });
                }
            }
        });
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

    // Methods for managing announcements

    /**
     * Adds an announcement to a specified event.
     *
     * @param eventId      The ID of the event to which the announcement will be added.
     * @param announcement The announcement to be added.
     * @param listener     Listener to be invoked when the operation is complete.
     */
    public void addAnnouncement(String eventId, Announcement announcement, OnCompleteListener<Void> listener) {
        if (announcement.getAnnouncementID() == null || announcement.getAnnouncementID().isEmpty()) {
            announcement.setAnnouncementID(UUID.randomUUID().toString());
        }
        FirebaseFirestore.getInstance()
                .collection("events").document(eventId)
                .collection("announcements").document(announcement.getAnnouncementID())
                .set(announcement)
                .addOnCompleteListener(listener);
    }

    /**
     * Loads announcements for a specified event.
     *
     * @param eventId  The ID of the event for which announcements will be loaded.
     * @param listener Listener to be invoked when the operation is successful.
     */
    public void loadAnnouncements(String eventId, OnSuccessListener<List<Announcement>> listener) {
        FirebaseFirestore.getInstance()
                .collection("events").document(eventId)
                .collection("announcements")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Announcement> announcements = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        announcements.add(document.toObject(Announcement.class));
                    }
                    listener.onSuccess(announcements);
                });
    }

    /**
     * Deletes an announcement from a specified event.
     *
     * @param eventID       The ID of the event from which the announcement will be deleted.
     * @param announcementID The ID of the announcement to be deleted.
     * @param onSuccess     Runnable to be executed when the operation is successful.
     * @param onError       Consumer to handle errors, accepting an error message.
     */
    public void deleteAnnouncement(String eventID, String announcementID, Runnable onSuccess, Consumer<String> onError) {
        DocumentReference announcementDocRef = FirebaseFirestore.getInstance().collection("events").document(eventID).collection("announcements").document(announcementID);
        announcementDocRef.delete()
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    /**
     * Updates an existing announcement.
     *
     * @param eventId      The ID of the event to which the announcement belongs.
     * @param announcement The updated announcement object.
     * @param listener     Listener to be invoked when the operation is complete.
     */
    public void updateAnnouncement(String eventId, Announcement announcement, OnCompleteListener<Void> listener) {
        FirebaseFirestore.getInstance()
                .collection("events").document(eventId)
                .collection("announcements").document(announcement.getAnnouncementID())
                .set(announcement, SetOptions.merge()) //  merge option to update existing document
                .addOnCompleteListener(listener);
    }

    // Methods for managing admin creation and abilities

    /**
     * Checks if the user is an admin.
     *
     * @param callback Callback to be invoked with the result of the check.
     */
    public void isAdmin(UserDocumentCallback callback) {
        DocumentReference adminDocRef = adminsRef.document(phoneID);
        adminDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                boolean exists = document.exists();
                callback.onCallback(exists);
            }
        });
    }

    /**
     * Grants admin privileges if the provided password matches.
     *
     * @param password The password to authenticate admin privilege grant.
     */
    public void becomeAdmin(String password) {
        String adminPassword = "12345";
        if (password.equals(adminPassword)) {
            addNewAdmin();
        }
    }

    /**
     * Adds a new admin to the system.
     */
    public void addNewAdmin() {
        deleteUser(phoneID);

        HashMap<String, String> data = new HashMap<>();
        data.put("ID", phoneID);

        adminsRef.document(phoneID).set(data);
    }

    /**
     * Retrieves URLs of event posters.
     *
     * @param callback Callback to be invoked with the list of poster URLs.
     */
    public void getPosterURLs(URLCallback callback) {
        ArrayList<String> urlList = new ArrayList<>();

        eventsRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                String posterURL = (document.getString("eventPosterURL").startsWith("https")) ? document.getString("eventPosterURL") : null;
                if (posterURL != null) { urlList.add(posterURL); }
            }
            callback.onCallback(urlList);
        });
    }

    /**
     * Retrieves URLs of user profile pictures.
     *
     * @param callback Callback to be invoked with the list of profile picture URLs.
     */
    public void getPFPURLs(URLCallback callback) {
        ArrayList<String> urlList = new ArrayList<>();

        usersRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                String pfpURL = (document.getString("profilePic").startsWith("https")) ? document.getString("profilePic") : null;
                if (pfpURL != null) { urlList.add(pfpURL); }
            }
            callback.onCallback(urlList);
        });
    }

    /**
     * Deletes a user from the system.
     *
     * @param ID The ID of the user to be deleted.
     */
    public void deleteUser(String ID) {
        DocumentReference userDocRef = usersRef.document(ID);
        CollectionReference registeredEventsCollection = userDocRef.collection("registeredEvents");
        CollectionReference createdEventsCollection = userDocRef.collection("createdEvents");

        registeredEventsCollection.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                String eventID = document.getId();

                DocumentReference eventDocRef = eventsRef.document(eventID);
                CollectionReference registeredUsersCollection = eventDocRef.collection("registeredUsers");

                registeredUsersCollection.document(ID).delete();

                registeredEventsCollection.document(eventID).delete();
            }
        });

        createdEventsCollection.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                String eventID = document.getId();

                DocumentReference eventDocRef = eventsRef.document(eventID);
                CollectionReference registeredUsersCollection = eventDocRef.collection("registeredUsers");

                registeredUsersCollection.get().addOnCompleteListener(task2 -> {
                    for (DocumentSnapshot document2 : task.getResult()) {
                        registeredUsersCollection.document(document2.getId()).delete();
                    }
                });

                eventDocRef.delete();
                createdEventsCollection.document(eventID).delete();
            }
        });

        userDocRef.delete();
    }

    /**
     * Deletes an event from the system.
     *
     * @param ID The ID of the event to be deleted.
     */
    public void deleteEvent(String ID) {
        DocumentReference eventDocRef = eventsRef.document(ID);
        CollectionReference registeredUsersCollection = eventDocRef.collection("registeredUsers");

        registeredUsersCollection.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                String userID = document.getId();

                DocumentReference userDocRef = usersRef.document(userID);
                CollectionReference registeredEventsCollection = userDocRef.collection("registeredEvents");
                registeredEventsCollection.document(ID).delete();

                registeredUsersCollection.document(userID).delete();
            }
        });

        usersRef.get().addOnCompleteListener(task -> {
            for (DocumentSnapshot document : task.getResult()) {
                String userID = document.getId();

                DocumentReference userDocRef = usersRef.document(userID);
                CollectionReference createdEventsCollection = userDocRef.collection("createdEvents");
                createdEventsCollection.document(ID).delete();
            }
        });

        eventDocRef.delete();
    }

    /**
     * Deletes usage of an image URL either as an event poster URL or a user profile picture URL.
     *
     * @param URL         The URL of the image to be deleted.
     * @param isPosterURL Boolean indicating whether the URL is for an event poster.
     */
    public void deleteImageUsage(String URL, boolean isPosterURL) {
        if (isPosterURL) {
            eventsRef.get().addOnCompleteListener(task -> {
                for (DocumentSnapshot document : task.getResult()) {
                    DocumentReference eventDocRef = eventsRef.document(document.getId());
                    String eventPosterURL = document.getString("eventPosterURL");

                    if (eventPosterURL.equals(URL)) {
                        eventDocRef.update("eventPosterURL", "https://firebasestorage.googleapis.com/v0/b/circleapp-2e84b.appspot.com/o/default_pics%2Fdefault_event_poster.webp?alt=media&token=e3a687a5-8ca8-4f25-9fbd-a9be6631ee0c");
                    }
                }
            });
        }
        else {
            usersRef.get().addOnCompleteListener(task -> {
                for (DocumentSnapshot document : task.getResult()) {
                    DocumentReference userDocRef = usersRef.document(document.getId());
                    String pfpURL = document.getString("profilePic");

                    if (pfpURL.equals(URL)) {
                        userDocRef.update("profilePic", "null");
                    }
                }
            });
        }
    }

    // start tracking check-ins for an event
    public void trackCheckIns(String eventId, Consumer<Integer> onAttendanceCountUpdated) {
        DocumentReference eventDocRef = eventsRef.document(eventId);
        eventDocRef.collection("checkedInUsers")
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w("FirebaseManager", "Listen failed.", e);
                        return;
                    }
                    int currentCount = value != null ? value.size() : 0;
                    onAttendanceCountUpdated.accept(currentCount);
                });
    }

}