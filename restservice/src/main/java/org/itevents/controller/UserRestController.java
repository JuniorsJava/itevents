package org.itevents.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.itevents.model.Event;
import org.itevents.model.Filter;
import org.itevents.model.User;
import org.itevents.model.builder.UserBuilder;
import org.itevents.service.CryptTokenService;
import org.itevents.service.security.Token;
import org.itevents.service.EventService;
import org.itevents.service.FilterService;
import org.itevents.service.RoleService;
import org.itevents.service.UserService;
import org.itevents.service.converter.FilterConverter;
import org.itevents.util.time.DateTimeUtil;
import org.itevents.wrapper.FilterWrapper;
import org.itevents.wrapper.TokenWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@Api("Users")
@RequestMapping("/users")
public class UserRestController {
    @Inject
    private UserService userService;
    @Inject
    private RoleService roleService;
    @Inject
    private EventService eventService;
    @Inject
    private FilterService filterService;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private CryptTokenService tokenService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "User's name", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "User password", required = true, dataType = "string", paramType = "query")
    })
    @RequestMapping(method = RequestMethod.POST, value = "login")
    @ApiOperation(value = "Generate authorization token")
    public ResponseEntity<TokenWrapper> login(@ModelAttribute("username") String username,
                                              @ModelAttribute("password") String password) {

        User user = userService.getUserByName(username);
        System.out.println("--------------User controller------------");
        System.out.println(user.getPassword().equals(passwordEncoder.encode(password)));
        System.out.println(user.getRole().getName());
        String token = tokenService.encrypt(new Token(username, user.getRole().getName()));
        System.out.println(token);
        System.out.println("-------------- END User controller------------");
        return new ResponseEntity<>(new TokenWrapper(token), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "New subscriber's name", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "New subscriber's password", required = true, dataType = "string", paramType = "query")
    })
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    @ApiOperation(value = "Registers new Subscriber ")
    public ResponseEntity registerNewSubscriber(@ModelAttribute("username") String username,
                                                @ModelAttribute("password") String password) {
        if (exists(username)) {
            return new ResponseEntity(HttpStatus.IM_USED);
        }
        User user = UserBuilder.anUser()
                .login(username)
                .password(passwordEncoder.encode(password))
                .role(roleService.getRoleByName("subscriber"))
                .build();
        userService.addUser(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "me")
    @ApiOperation(value = "Returns authorization user")
    public ResponseEntity<User> getMe() {
        return new ResponseEntity<>(userService.getAuthorizedUser(),HttpStatus.OK);
    }

    private boolean exists(String username) {
        return userService.getUserByName(username) != null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/subscribe")
    @ApiOperation(value = "Activates authorized user's e-mail subscription with filter")
    public ResponseEntity activateSubscription(@ModelAttribute FilterWrapper wrapper) {
        Filter filter = new FilterConverter().toFilter(wrapper);
        filter.setCreateDate(DateTimeUtil.getNowDate());
        User user = userService.getAuthorizedUser();
        filterService.addFilter(user, filter);
        userService.activateUserSubscription(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/unsubscribe")
    @ApiOperation(value = "Deactivates authorized user's e-mail subscription")
    public ResponseEntity deactivateSubscription() {
        User user = userService.getAuthorizedUser();
        userService.deactivateUserSubscription(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user_id}/events")
    @ApiOperation(value = "Returns list of events, to which user is assigned")
    public ResponseEntity<List<Event>> getEventsByUser(@PathVariable("user_id") int userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            List<Event> events = eventService.getEventsByUser(user);
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
    }
}