package com.example.circleapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.QRCode.GenerateQRActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GenerateQRActivityTest {
    @Rule
    public ActivityScenarioRule<GenerateQRActivity> scenarioRule = new ActivityScenarioRule<>(GenerateQRActivity.class);

    @Test
    public void testQRCodeGeneration() {
        // Create a new event
        Event event = new Event();
        event.setID("testEventID");

        // Create an intent with the event as an extra
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GenerateQRActivity.class);
        intent.putExtra("event", event);

        // Launch the activity with the intent
        scenarioRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Check if the QR code image view is not null
        onView(withId(R.id.qrImage)).check((view, noViewFoundException) -> {
            assertNotNull(view);
        });
    }

    @Test
    public void testQRCodeSharing() {
        // Create a new event
        Event event = new Event();
        event.setID("testEventID");

        // Create an intent with the event as an extra
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GenerateQRActivity.class);
        intent.putExtra("event", event);

        // Launch the activity with the intent
        scenarioRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Click on the share button
        onView(withId(R.id.share_QR_button)).perform(click());
    }
}

