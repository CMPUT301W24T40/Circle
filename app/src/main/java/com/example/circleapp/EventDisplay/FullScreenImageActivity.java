package com.example.circleapp.EventDisplay;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.circleapp.R;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImageView = findViewById(R.id.full_screen_image);
        ConstraintLayout backgroundLayout = findViewById(R.id.background_layout);

        // Load the image into the ImageView
        String imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this).load(imageUrl).into(fullScreenImageView);

        // Apply the zoom in animation to the ImageView
        Animation zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        fullScreenImageView.startAnimation(zoomInAnimation);

        // Apply the fade in animation to the background
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        backgroundLayout.startAnimation(fadeInAnimation);

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Apply the zoom out animation to the ImageView
                Animation zoomOutAnimation = AnimationUtils.loadAnimation(FullScreenImageActivity.this, R.anim.zoom_out);
                fullScreenImageView.startAnimation(zoomOutAnimation);

                // Apply the fade out animation to the background
                Animation fadeOutAnimation = AnimationUtils.loadAnimation(FullScreenImageActivity.this, R.anim.fade_out);
                backgroundLayout.startAnimation(fadeOutAnimation);

                // Delay the finish of the activity to allow the animations to play
                new Handler().postDelayed(FullScreenImageActivity.this::finish, 200);
            }
        });
    }
}