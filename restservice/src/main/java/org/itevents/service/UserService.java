package org.itevents.service;

import org.itevents.model.Event;
import org.itevents.model.User;
import org.itevents.util.OneTimePassword.OtpGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void addSubscriber(String name, String password) throws Exception;

    User getUser(int userId);

    User getUserByName(String name);

    User getAuthorizedUser();

    void activateUserSubscription(User user);

    void deactivateUserSubscription(User user);

    List<User> getAllUsers();

    void setOtpToUser(User user, OtpGenerator otpGenerator);

    User getUserByOtp(OtpGenerator otp);

    void activateUserWithOtp(String password);

    void sendEmailWithActivationLink(User user) throws Exception;

    List<User> getSubscribedUsers();

    List<User> getUsersByEvent(Event event);

    void setAndEncodeUserPassword(User user, String password);

    String getUserPassword(User user);

    void checkPassword(User user, String password);
}
