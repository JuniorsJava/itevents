package org.itevents.integration;

import org.itevents.dao.model.Event;
import org.itevents.dao.model.User;
import org.itevents.service.EventService;
import org.itevents.service.UserService;
import org.itevents.service.VisitLogService;
import org.itevents.service.exception.*;
import org.itevents.test_utils.BuilderUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by vaa25 on 04.01.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml",
                        "classpath:applicationContextTestAddon.xml",
                        "classpath:mvc-dispatcher-servlet.xml",
                        "classpath:spring-security.xml"})
@WebAppConfiguration
public class ControllerHandlerTest {
    @Inject
    private WebApplicationContext context;
    private MockMvc mvc;
    @Inject
    private EventService eventService;
    @Inject
    private UserService userService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void shouldNotAssignUserToEventIfEventIsAbsent() throws Exception {
        int absentId = 0;

        doThrow(EntityNotFoundServiceException.class).when(eventService).assignAuthorizedUserToEvent(absentId);

        mvc.perform(post("/events/" + absentId + "/assign"))
                .andExpect(status().isNotFound());

        verify(eventService).assignAuthorizedUserToEvent(absentId);
    }

    @Test
    public void shouldNotAssignUserToEventIfEventDateIsPassed() throws Exception {
        Event event = BuilderUtil.buildEventJava();

        doThrow(TimeCollisionServiceException.class).when(eventService).assignAuthorizedUserToEvent(event.getId());

        mvc.perform(post("/events/" + event.getId() + "/assign"))
                .andExpect(status().isBadRequest());

        verify(eventService).assignAuthorizedUserToEvent(event.getId());
    }

    @Test
    public void shouldNotRegisterExistingSubscriber() throws Exception {
        String name = "SubscriberInDatabase";
        String password = "anyPassword";

        doThrow(EntityAlreadyExistsServiceException.class).when(userService).addSubscriber(name, password);

        mvc.perform(post("/users/register")
                .param("username", name)
                .param("password", password))
                .andExpect(status().is(422));
    }

    @Test
    public void shouldNotFoundIfEventIsAbsent() throws Exception {
        Event event = BuilderUtil.buildEventRuby();

        when(eventService.redirectToEventSite(event.getId())).thenThrow(EntityNotFoundServiceException.class);

        mvc.perform(get("/events/" + event.getId() + "/register"))
                .andExpect(status().isNotFound());

        verify(eventService).redirectToEventSite(event.getId());
    }

    @Test
    public void shouldReturn404IfUserIsAbsent() throws Exception {
        int absentId = 0;

        when(userService.getUser(absentId)).thenThrow(EntityNotFoundServiceException.class);

        mvc.perform(get("/users/" + absentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAssignUserToEventIfAssigned() throws Exception {
        User user = BuilderUtil.buildUserAnakin();
        Event event = BuilderUtil.buildEventJava();

        when(eventService.getFutureEvent(event.getId())).thenReturn(event);
        when(userService.getAuthorizedUser()).thenReturn(user);
        doThrow(ActionAlreadyDoneServiceException.class)
            .when(eventService).assignAuthorizedUserToEvent(event.getId());

        mvc.perform(post("/events/" + event.getId() + "/assign"))
                .andExpect(status().isConflict());

        reset(eventService);
    }

    @Test
    public void shouldNotUnassignAssignUserFromEventIfParamLengthIsNotValid() throws Exception {
        Event event = BuilderUtil.buildEventJava();
        User user = BuilderUtil.buildUserAnakin();
        ArrayList <Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(event);
        String invalidParameter = "invalid";
        for (int i = 0; i <250 ; i++) {
            invalidParameter = invalidParameter.concat("s");
        }

        when(eventService.getEvent(event.getId())).thenReturn(event);
        when(userService.getAuthorizedUser()).thenReturn(user);
        when(eventService.getEventsByUser(user)).thenReturn(expectedEvents);

        mvc.perform(post("/events/" + event.getId() + "/unassign")
                .param("unassign_reason", invalidParameter))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUnassignUserIfParameterIsEmpty() throws Exception {
        Event event = BuilderUtil.buildEventJava();
        User user = BuilderUtil.buildUserAnakin();
        ArrayList <Event> expectedEvents = new ArrayList<>();
        expectedEvents.add(event);
        String validParameter = "";

        when(eventService.getEvent(event.getId())).thenReturn(event);
        when(userService.getAuthorizedUser()).thenReturn(user);
        when(eventService.getEventsByUser(user)).thenReturn(expectedEvents);

        mvc.perform(post("/events/" + event.getId() + "/unassign")
                .param("unassign_reason", validParameter))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldExpect404IfOtpNotValid() throws Exception {
        String otp = "NotValidOtp";

        doThrow(EntityNotFoundServiceException.class)
            .when(userService).activateUserWithOtp(otp);

        mvc.perform(get("/users/activate/"+ otp))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldExpect401WhenLoggingWithIncorrectPassword() throws Exception {
        String invalidPassword = "invalidPassword";

        User user = BuilderUtil.buildUserAnakin();

        when(userService.getUserByName(user.getLogin())).thenReturn(user);

        doThrow(AuthenticationServiceException.class)
                .when(userService).checkPassword(user, invalidPassword);

        mvc.perform(post("/users/login")
        .param("username", user.getLogin())
        .param("password", invalidPassword))
                .andExpect(status().isUnauthorized());

    }
}