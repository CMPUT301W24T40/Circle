package com.example.circleapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.circleapp.Profile.MakeProfileActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MakeProfileActivityTest {

    @Rule
    public ActivityScenarioRule<MakeProfileActivity> scenario = new ActivityScenarioRule<>(MakeProfileActivity.class);

    @Test
    public void testUserProfileCreation() {
        // Type text into the input fields
        onView(withId(R.id.fname_edit)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.lname_edit)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.edit_email)).perform(typeText("john.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_number)).perform(typeText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.edit_homepage)).perform(typeText("www.johndoe.com"), closeSoftKeyboard());

         // Check that the "Confirm" button is displayed and clickable
        onView(withId(R.id.confirm_edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.confirm_edit_button)).check(matches(isClickable()));
        onView(withId(R.id.confirm_edit_button)).check(matches(isEnabled()));

    }

    @Test
    public void testProfilePicSelectionDialog() {
        onView(withId(R.id.edit_pfp)).perform(click());

    }

    @Test
    public void testEmptyFirstNameShowsDialog() {
        onView(withId(R.id.confirm_edit_button)).perform(click());
        onView(withText("Please input at least a first name")).check(matches(withText("Please input at least a first name")));
    }

}