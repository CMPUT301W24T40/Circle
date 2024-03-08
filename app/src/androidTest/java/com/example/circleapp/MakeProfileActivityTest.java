package com.example.circleapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.circleapp.Profile.MakeProfileActivity;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MakeProfileActivityTest {

    @Rule
    public ActivityScenarioRule<MakeProfileActivity> scenario = new ActivityScenarioRule<>(MakeProfileActivity.class);

    @Test
    public void testConfirmButton() {
        onView(withId(R.id.fname_edit)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.lname_edit)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.edit_email)).perform(typeText("john.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_number)).perform(typeText("1234567890"), closeSoftKeyboard());

        // Click on the confirm button
        onView(withId(R.id.confirm_edit_button)).perform(click());

    }

    @Test
    public void testGeoLocationCheckbox() {
        // Click on the geolocation checkbox
        onView(withId(R.id.edit_geolocation)).perform(click());

        // Check if the checkbox is checked
        onView(withId(R.id.edit_geolocation)).check(matches(isChecked()));
    }

}