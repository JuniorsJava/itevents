package org.itevents.service;

import org.itevents.dao.EventDao;
import org.itevents.model.Event;
import org.itevents.model.Location;
import org.itevents.model.User;
import org.itevents.parameter.FilteredEventsParameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service("eventService")
@Transactional
public class EventServiceImpl implements EventService {

    @Inject
    private EventDao eventDao;

    @Override
    public void addEvent(Event event) {
        eventDao.addEvent(event);
    }

    @Override
    public Event getEvent(int id) {
        return eventDao.getEvent(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }

    @Override
    public List<Event> getEventsInRadius(Location location, int radius) {
        return eventDao.getEventsInRadius(location, radius);
    }

    @Override
    public Event removeEvent(Event event) {
        Event deletingEvent = eventDao.getEvent(event.getId());
        if (deletingEvent != null) {
            eventDao.removeEvent(event);
        }
        return deletingEvent;
    }

    @Override
    public List<Event> getFilteredEvents(FilteredEventsParameter params) {
        List<Event> result;
        try {
            result = eventDao.getFilteredEvents(params);
        } catch (Exception e) {
            result = new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<User> getVisitors(Event event) {
        List<User> visitors;
        try {
            visitors = eventDao.getAllVisitors(event);
        }catch (Exception e) {
            visitors = new ArrayList<>();
        }

        return visitors;
    }
}