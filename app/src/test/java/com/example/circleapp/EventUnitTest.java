package com.example.circleapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.example.circleapp.BaseObjects.Event;

public class EventUnitTest {

    @Test
    public void testGettersAndSetters() {

        Event event = new Event();

        // Set values using setters
        event.setID("123");
        event.setEventName("Event1");
        event.setLocation("Location1");
        event.setDate("2024-03-01");
        event.setTime("11:00 AM");
        event.setDescription("Description1");
        event.setEventPoster(12345);

        // Check using getters
        assertEquals("123", event.getID());
        assertEquals("Event1", event.getEventName());
        assertEquals("Location1", event.getLocation());
        assertEquals("2024-03-01", event.getDate());
        assertEquals("11:00 AM", event.getTime());
        assertEquals("Description1", event.getDescription());
        assertEquals(12345, event.getEventPoster());
    }

}