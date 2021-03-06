package org.itevents.service.transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itevents.dao.EventDao;
import org.itevents.dao.exception.EntityNotFoundDaoException;
import org.itevents.dao.model.Event;
import org.itevents.dao.model.Filter;
import org.itevents.dao.model.User;
import org.itevents.dao.model.VisitLog;
import org.itevents.dao.model.builder.VisitLogBuilder;
import org.itevents.service.EventService;
import org.itevents.service.UserService;
import org.itevents.service.VisitLogService;
import org.itevents.service.exception.ActionAlreadyDoneServiceException;
import org.itevents.service.exception.EntityNotFoundServiceException;
import org.itevents.service.exception.TimeCollisionServiceException;
import org.itevents.util.time.DateTimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service("eventService")
@Transactional
public class MyBatisEventService implements EventService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Inject
    private EventDao eventDao;
    @Inject
    private UserService userService;
    @Inject
    private VisitLogService visitLogService;

    @Override
    public void addEvent(Event event) {
        eventDao.addEvent(event);
        eventDao.addEventTechnology(event);
    }

    @Override
    public Event getEvent(int id) {
        try {
            return eventDao.getEvent(id);
        } catch (EntityNotFoundDaoException e) {
            LOGGER.error(e.getMessage());
            throw new EntityNotFoundServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }

    @Override
    public void assignAuthorizedUserToEvent(int futureEventId) {
        Event event = getFutureEvent(futureEventId);
        User user = userService.getAuthorizedUser();
        assignUserToEvent(user, event);
    }

    private void assignUserToEvent(User user, Event futureEvent) {
        if (isAssigned(user, futureEvent)) {
            String message=user.getLogin() + " already assigned to " + futureEvent.getTitle();
            LOGGER.error(message);
            throw new ActionAlreadyDoneServiceException(message);
        } else {
            eventDao.assignUserToEvent(user, futureEvent);
        }
    }

    @Override
    public void unassignAuthorizedUserFromEvent(int futureEventId, String unassignReason) {
        Event event = getEvent(futureEventId);
        User user = userService.getAuthorizedUser();
        Date unassignDate = DateTimeUtil.getNowDate();
        unassignUserFromEvent(user, event, unassignDate, unassignReason);
    }

    private void unassignUserFromEvent(User user, Event event, Date unassignDate, String unassignReason) {
        if (isAssigned(user, event)) {
            eventDao.unassignUserFromEvent(user, event, unassignDate, unassignReason);
        } else {
            String message=user.getLogin() + " already unassigned from "+ event.getTitle();
            LOGGER.error(message);
            throw new ActionAlreadyDoneServiceException(message);
        }
    }

    @Override
    public List<Event> getEventsByUser(User user) {
        return eventDao.getEventsByUser(user);
    }

    @Override
    public Event getFutureEvent(int eventId) {
        Event event = getEvent(eventId);
        if (event.getEventDate().before(new Date())) {
            String message = String.format("Try to get event id=%s with date %s as future event", eventId, event.getEventDate().toString());
            LOGGER.error(message);
            throw new TimeCollisionServiceException(message);
        }
        return event;
    }

    @Override
    public List<Event> getFilteredEvents(Filter filter) {
        return eventDao.getFilteredEvents(filter);
    }

    @Override
    public List<Event> getFilteredEventsWithRating(Filter filter){
        return eventDao.getFilteredEventsWithRating(filter);
    }

    private boolean isAssigned(User user, Event event) {
        return getEventsByUser(user).contains(event);
    }

    @Override
    public String redirectToEventSite(int eventId) {
        Event event = getEvent(eventId);
        User user = userService.getAuthorizedUser();
        VisitLog visitLog = VisitLogBuilder.aVisitLog()
                .event(event)
                .user(user)
                .date(DateTimeUtil.getNowDate())
                .build();
        visitLogService.addVisitLog(visitLog);
        return event.getRegLink();
    }
}