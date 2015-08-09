package org.itevents;

import org.itevents.model.Event;
import org.itevents.model.Technology;
import org.itevents.parameter.FilteredEventsParameter;
import org.itevents.service.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventServiceTest {

    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    private final static int ID_3 = 3;
    private final static int ID_4 = 4;
    private final static int ID_6 = 6;
    private final static int ID_7 = 7;
    private static EventService eventService;
    private static TechnologyService technologyService;
    private static CityService cityService;

    @BeforeClass
    public static void setup() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        eventService = context.getBean("eventService", EventServiceImpl.class);
        technologyService = context.getBean("techTagService", TechnologyService.class);
        cityService = context.getBean("cityService", CityServiceImpl.class);
    }

    @AfterClass
    public static void teardown() {
        eventService = null;
    }

    //todo this test must do branch №6
    @Test
    public void testGetEventById() {
        Event returnedEvent = eventService.getEvent(ID_1);
        assertNotNull(returnedEvent);
    }

    @Test
    public void testGetFilteredEventsKyivJava() {
        int javaId = 1;
        int kyivId = 1;
        List<Technology> testTechnologies = new ArrayList<>();
        testTechnologies.add(technologyService.getTechTag(javaId));
        List<Event> expectedEvents = new ArrayList<>();
        FilteredEventsParameter params = new FilteredEventsParameter();
        params.setTechnologies(testTechnologies);
        params.setCity(cityService.getCity(kyivId));
        expectedEvents.add(eventService.getEvent(ID_1));
        List<Event> returnedEvents = eventService.getFilteredEvents(params);
        assertEquals(expectedEvents, returnedEvents);
    }

    @Test
    public void testGetFilteredEventsBoyarkaPayed() {
        int boyarkaId = 3;
        List<Event> expectedEvents = new ArrayList<>();
        FilteredEventsParameter params = new FilteredEventsParameter();
        params.setCity(cityService.getCity(boyarkaId));
        params.setFree(false);
        expectedEvents.add(eventService.getEvent(ID_6));
        List<Event> returnedEvents = eventService.getFilteredEvents(params);
        assertEquals(expectedEvents, returnedEvents);
    }

    @Test
    public void testGetFilteredEventsPhpAntSql() {
        int phpId = 3;
        int antId = 7;
        int sqlId = 10;
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(eventService.getEvent(ID_4));
        expectedEvents.add(eventService.getEvent(ID_3));
        expectedEvents.add(eventService.getEvent(ID_7));
        List<Technology> testTechnologies = new ArrayList<>();
        testTechnologies.add(technologyService.getTechTag(phpId));
        testTechnologies.add(technologyService.getTechTag(antId));
        testTechnologies.add(technologyService.getTechTag(sqlId));
        FilteredEventsParameter params = new FilteredEventsParameter();
        params.setTechnologies(testTechnologies);
        List<Event> returnedEvents = eventService.getFilteredEvents(params);
        assertEquals(expectedEvents, returnedEvents);
    }

    @Test
    public void testGetFilteredEventsInRadius() {
        double testLatitude = 50.454605;
        double testLongitude = 30.403965;
        int testRadius = 5000;
        List<Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(eventService.getEvent(ID_2));
        expectedEvents.add(eventService.getEvent(ID_3));
        FilteredEventsParameter params = new FilteredEventsParameter();
        params.setLatitude(testLatitude);
        params.setLongitude(testLongitude);
        params.setRadius(testRadius);
        List<Event> returnedEvents = eventService.getFilteredEvents(params);
        assertEquals(expectedEvents, returnedEvents);
    }
}
