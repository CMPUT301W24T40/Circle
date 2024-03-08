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
        event.setEventName("Sample Event");
        event.setLocation("Sample Location");
        event.setDate("2024-03-01");
        event.setTime("10:00 AM");
        event.setDescription("Sample Description");
        event.setEventPoster(12345);

        // Check using getters
        assertEquals("123", event.getID());
        assertEquals("Sample Event", event.getEventName());
        assertEquals("Sample Location", event.getLocation());
        assertEquals("2024-03-01", event.getDate());
        assertEquals("10:00 AM", event.getTime());
        assertEquals("Sample Description", event.getDescription());
        assertEquals(12345, event.getEventPoster());
    }
}