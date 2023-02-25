package at.technikum.application.controller;

import at.technikum.application.model.Credentials;
import at.technikum.application.model.User;
import at.technikum.application.service.UserService;
import at.technikum.httpserver.BadRequestException;
import at.technikum.httpserver.HttpStatus;
import at.technikum.httpserver.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestUserControllerTest {

    @Mock
    UserService userService;
    @InjectMocks
    RestUserController restUserController;

    @Test
    void testRegisterNewUser(){
        Credentials credentials = new Credentials("test", "user");

        Response res = restUserController.register(credentials);

        assertEquals(HttpStatus.CREATED, res.getHttpStatus());
        assertEquals("User test successfully registered", res.getBody());
    }

    @Test
    void testUsernameOnlyUsedOnce(){
        Credentials credentials = new Credentials("test", "user");
        when(userService.findUserByUsername("test")).thenReturn(new User("test", "user"));
        try{
            restUserController.register(credentials);
            fail();
        } catch(BadRequestException e){
            assertEquals(e.getMessage(), "User with username test already exists");
        }
    }

    @Test
    void loginWorks(){
        String name = "test";
        String pw = "user";
        Credentials credentials = new Credentials(name, pw);
        User user = new User(name, pw);
        when(userService.findUserByUsername(name)).thenReturn(user);
        when(userService.loginUser(credentials)).thenReturn(true);

        Response res = restUserController.login(credentials);

        assertEquals(HttpStatus.OK, res.getHttpStatus());
    }

    @Test
    void loginDoesntWorkWhenNotRegistrated(){
        try{
            restUserController.login(new Credentials("text", "user"));
            fail();
        } catch(BadRequestException e){
            assertEquals(e.getMessage(), "User with username text doesn't exist");
        }
    }
}
