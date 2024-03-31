package com.example.circleapp.Firebase;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * This class is used to manage images with Firebase Cloud Storage.
 */
public class ImageManager {
    private static final int PICK_IMAGE = 1;
    private final Activity activity;
    private final ImageView imageView;
    private Uri imageUri;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    // Constructors

    /**
     * Constructs an ImageManager object with specified activity and null ImageView.
     *
     * @param activity The activity associated with the ImageManager.
     */
    public ImageManager(Activity activity) {
        this.activity = activity;
        this.imageView = null;
    }

    /**
     * Constructs an ImageManager object with specified activity and ImageView.
     *
     * @param activity The activity associated with the ImageManager.
     * @param imageView The ImageView for displaying images.
     */
    public ImageManager(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    // Callback interfaces

    /**
     * Interface for asynchronous callback when retrieving images.
     */
    public interface ImagesCallback {
        /**
         * Callback method invoked when images are retrieved.
         *
         * @param imagesList The list of image URIs.
         */
        void onCallback(ArrayList<Uri> imagesList);
    }

    // Methods associated with Admin

    /**
     * Retrieves posters from Firebase Cloud Storage.
     *
     * @param callback The callback interface for handling the retrieved images.
     */
    public void getPosters(ImagesCallback callback) {
        ArrayList<Uri> imagesList = new ArrayList<>();

        firebaseManager.getPosterURLs(urlList -> {

            for (String url : urlList) {
                StorageReference imageRef = storage.getReferenceFromUrl(url);

                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imagesList.add(uri);

                    callback.onCallback(imagesList);
                });
            }
        });
    }

    /**
     * Retrieves profile pictures from Firebase Cloud Storage.
     *
     * @param callback The callback interface for handling the retrieved images.
     */
    public void getPFPs(ImagesCallback callback) {
        ArrayList<Uri> imagesList = new ArrayList<>();

        firebaseManager.getPFPURLs(urlList -> {

            for (String url : urlList) {
                StorageReference imageRef = storage.getReferenceFromUrl(url);

                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imagesList.add(uri);

                    callback.onCallback(imagesList);
                });
            }
        });
    }

    /**
     * Deletes an image from Firebase Cloud Storage.
     *
     * @param imageURL The URL of the image to be deleted.
     */
    public void deleteImage(String imageURL) {
        StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
        imageRef.delete();

        if (imageURL.contains("profile_pictures")) {
            firebaseManager.deleteImageUsage(imageURL, false);
        }
        else if (imageURL.contains("event_posters")) {
            firebaseManager.deleteImageUsage(imageURL, true);
        }
    }

    // Methods to manage image selection and upload

    /**
     * Checks if an ImageView has an image set.
     *
     * @return true if an image is set, false otherwise.
     */
    public boolean hasImage() {
        return imageView.getDrawable() != null;
    }

    /**
     * Initiates image selection process.
     */

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * Uploads a poster image to Firebase storage.
     *
     * @param onSuccessListener Listener for a successful upload.
     */
    public void uploadPosterImage(OnSuccessListener<String> onSuccessListener) {
        if (imageUri != null) {
            StorageReference eventPosterRef = storageRef.child("event_posters/" + System.currentTimeMillis() + ".jpg");
            eventPosterRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Log.d("ImageManager", "Image uploaded, download URL: " + downloadUrl);
                        onSuccessListener.onSuccess(downloadUrl);
                    }));
        }
    }

    /**
     * Uploads a profile picture to Firebase storage.
     *
     * @param onSuccessListener Listener for a successful upload.
     */
    public void uploadProfilePictureImage(OnSuccessListener<String> onSuccessListener) {
        if (imageUri != null) {
            StorageReference profilePicRef = storageRef.child("profile_pictures/" + System.currentTimeMillis() + ".jpg");
            profilePicRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Log.d("ImageManager", "Image uploaded, download URL: " + downloadUrl);
                        onSuccessListener.onSuccess(downloadUrl);
                    }));
        }
    }

    /**
     * Handles the result of image selection activity.
     *
     * @param requestCode The request code.
     * @param resultCode The result code.
     * @param data The intent data.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
        else {
        Toast.makeText(activity.getBaseContext(), "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}

