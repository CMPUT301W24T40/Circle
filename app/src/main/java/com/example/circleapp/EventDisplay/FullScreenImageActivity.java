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

/**
 * This class is used to display an image in full screen mode. The image is passed to this activity
 * via an intent from another activity. The image is displayed with animations.
 */
public class FullScreenImageActivity extends AppCompatActivity {
    /**
     * Called when the activity is starting. This method is used to set the content view to the
     * full screen image layout and display the image with a zoom-in animation and the background
     * layout with a fade-in animation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImageView = findViewById(R.id.full_screen_image);
        ConstraintLayout backgroundLayout = findViewById(R.id.background_layout);

        String imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this).load(imageUrl).into(fullScreenImageView);

        Animation zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        fullScreenImageView.startAnimation(zoomInAnimation);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        backgroundLayout.startAnimation(fadeInAnimation);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            /**
             * Called when the back button is pressed. This method is used to animate the image with
             * a zoom-out animation and the background layout with a fade-out animation before finishing
             * the activity.
             */
            @Override
            public void handleOnBackPressed() {
                Animation zoomOutAnimation = AnimationUtils.loadAnimation(FullScreenImageActivity.this, R.anim.zoom_out);
                fullScreenImageView.startAnimation(zoomOutAnimation);

                Animation fadeOutAnimation = AnimationUtils.loadAnimation(FullScreenImageActivity.this, R.anim.fade_out);
                backgroundLayout.startAnimation(fadeOutAnimation);

                new Handler().postDelayed(FullScreenImageActivity.this::finish, 200);
            }
        });
    }
}