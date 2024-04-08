package com.example.circleapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SendNotificationActivityTest {
    @Before
    public void setup() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), SendNotificationActivity.class);
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add("dummyToken");
        Bundle extras = new Bundle();
        extras.putStringArrayList("tokens", tokens);
        extras.putString("event name", "Test Event Name");
        intent.putExtras(extras);

        ActivityScenario.launch(intent);
    }
    @Test
    public void notificationValidInputs() {
        onView(withId(R.id.notif_title)).perform(typeText("Test Title"), closeSoftKeyboard());
        onView(withId(R.id.notif_body)).perform(typeText("Test Body Message"), closeSoftKeyboard());

        // Attempt to send the notification
        onView(withId(R.id.send_notif_button)).perform(click());

    }
    @Test
    public void emptyMessageShowsAlertDialog() {
        // Type title, leave body empty
        onView(withId(R.id.notif_title)).perform(typeText("Test Title"), closeSoftKeyboard());

        onView(withId(R.id.send_notif_button)).perform(click());
        // Verify AlertDialog is displayed
        onView(withText("Please write a message to send to your attendees!")).check(matches(isDisplayed()));
    }
}