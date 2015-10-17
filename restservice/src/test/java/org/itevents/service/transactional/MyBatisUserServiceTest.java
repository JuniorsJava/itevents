package org.itevents.service.transactional;

import org.itevents.dao.UserDao;
import org.itevents.model.User;
import org.itevents.service.UserService;
import org.itevents.test_utils.BuilderUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by vaa25 on 17.10.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:spring-security.xml"})
@Transactional
public class MyBatisUserServiceTest {

    @InjectMocks
    @Inject
    private UserService userService;
    @Mock
    private UserDao userDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFindUserById() throws Exception {
        User expectedUser = BuilderUtil.buildUserVlasov();
        when(userDao.getUser(expectedUser.getId())).thenReturn(expectedUser);
        User returnedUser = userService.getUser(expectedUser.getId());
        verify(userDao).getUser(expectedUser.getId());
        assertEquals(expectedUser, returnedUser);

    }

    @Test
//    @WithMockUser("testUser")
    public void shouldFindAuthorizedUser() {
        User expectedUser = BuilderUtil.buildUserTest();
        when(userDao.getUserByName(expectedUser.getLogin())).thenReturn(expectedUser);
        User returnedUser = userService.getAuthorizedUser();
        assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldAddUser() throws Exception {
        User testUser = BuilderUtil.buildUserTest();
        userService.addUser(testUser);
        verify(userDao).addUser(testUser);
    }

    @Test
    public void shouldGetAllUsers() {
        userService.getAllUsers();
        verify(userDao).getAllUsers();
    }

    @Test
    public void shouldRemoveUser() {
        User expectedUser = BuilderUtil.buildUserTest();
        when(userDao.getUser(expectedUser.getId())).thenReturn(expectedUser);
        doNothing().when(userDao).removeUser(expectedUser);
        User returnedUser = userService.removeUser(expectedUser);
        assertEquals(expectedUser, returnedUser);
    }

    @Test
    public void shouldNotRemoveUserWhenItIsNotExisting() {
        User testUser = BuilderUtil.buildUserTest();
        when(userDao.getUser(testUser.getId())).thenReturn(null);
        doNothing().when(userDao).removeUser(testUser);
        User returnedUser = userService.removeUser(testUser);
        assertNull(returnedUser);
    }
}