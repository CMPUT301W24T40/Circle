package com.example.circleapp;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

import com.example.circleapp.BaseObjects.Attendee;

//PROFILE PIC COMPONENT IS EXCLUDED AT THE MOMENT

public class AttendeeTest {

    @Test
    public void testAttendeeConstructor() {
        String ID = "123";
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String phoneNumber = "123456789";

        Attendee attendee = new Attendee(ID, firstName, lastName, email, phoneNumber, null);

        // Verify the constructor
        assertEquals(ID, attendee.getID());
        assertEquals(firstName, attendee.getFirstName());
        assertEquals(lastName, attendee.getLastName());
        assertEquals(email, attendee.getEmail());
        assertEquals(phoneNumber, attendee.getPhoneNumber());
    }

    @Test
    public void testAttendeeSetters() {
        // sample Attendee object
        Attendee attendee = new Attendee("123", "John", "Doe", "john@example.com", "123456789", null);

        // Update info
        String newFirstName = "Jane";
        String newLastName = "Smith";
        String newEmail = "jane@example.com";
        String newPhoneNumber = "987654321";

        attendee.setFirstName(newFirstName);
        attendee.setLastName(newLastName);
        attendee.setEmail(newEmail);
        attendee.setPhoneNumber(newPhoneNumber);

        // Check that the setter methods update correctly
        assertEquals(newFirstName, attendee.getFirstName());
        assertEquals(newLastName, attendee.getLastName());
        assertEquals(newEmail, attendee.getEmail());
        assertEquals(newPhoneNumber, attendee.getPhoneNumber());
    }
}