package com.example.circleapp.Profile;

/**
 * A callback interface that defines a method that is used
 * when the existence of a profile is checked.
 */
public interface ProfileExistenceCallback {

    /**
     * Called when the existence of a profile is checked.
     *
     * @param exists Indicates whether the profile exists or not.
     */
    void onProfileExistenceChecked(boolean exists);
}

