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
 * This class is used to manage images with Firebase Storage.
 */
public class ImageManager {
    private static final int PICK_IMAGE = 1;
    private final Activity activity;
    private final ImageView imageView;
    private Uri imageUri;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();

    // please leave this extra constructor
    public ImageManager(Activity activity) {
        this.activity = activity;
        this.imageView = null;
    }

    public ImageManager(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    public interface ImagesCallback {
        void onCallback(ArrayList<Uri> imagesList);
    }

    public void getImages(ImagesCallback callback) {
        ArrayList<Uri> imagesList = new ArrayList<>();

        FirebaseManager firebaseManager = FirebaseManager.getInstance();
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

    public boolean hasImage() {
        return imageView.getDrawable() != null;
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        } else {
        Toast.makeText(
                activity.getBaseContext(),
                "Image Not Selected",
                Toast.LENGTH_SHORT).show();
        }
    }
}

