package com.example.circleapp;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.circleapp.BaseObjects.Event;
import com.example.circleapp.EventDisplay.CreatedEventDetailsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventDetailsActivityTest {

    @Before
    public void setUp() {
        // Create a mock event
        Event mockEvent = new Event("123", "Sample Event", "Location A", "2024-01-01", "18:00", "This is a sample event description.");
        mockEvent.setCapacity("100");
        mockEvent.setEventPosterURL("http://example.com/poster.jpg");

        // Create an intent that includes mock event
        Intent intent = new Intent(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(), CreatedEventDetailsActivity.class);
        intent.putExtra("event", mockEvent);

        ActivityScenario.launch(intent);
    }

    /**
     * Tests the UI components' visibility in the EventDetailsActivity.
     */
    @Test
    public void testUIVisibility() {
        // Check for the visibility
        Espresso.onView(ViewMatchers.withId(R.id.event_details_name)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.event_details_location)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.event_details_date)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.event_details_time)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.event_details_description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.event_details_capacity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.event_details_poster)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }
}