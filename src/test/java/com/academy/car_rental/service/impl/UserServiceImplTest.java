package com.academy.car_rental.service.impl;


import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.model.repository.UserRepository;
import com.academy.car_rental.service.EmailService;
import com.academy.car_rental.service.OrderService;
import com.academy.car_rental.service.UUIDService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private OrderService orderService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UUIDService uuidService;


    private User user1;
    private User user2;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {

        user1 = User.builder()
                .id(1)
                .username("qwerty")
                .password("qwerty")
                .name("qwerty")
                .surname("qwerty")
                .phoneNumber("375293579518")
                .email("qwerty@gmail.com")
                .verificationStatus(true)
                .verificationCode(null)
                .build();
        user2 = User.builder()
                .id(2)
                .username("asdfg")
                .password("asdfgh")
                .name("asdfg")
                .surname("asdfg")
                .phoneNumber("375293579520")
                .email("asdf@gmail.com")
                .verificationStatus(false)
                .verificationCode("cvbnmzx")
                .build();
        userDto = new UserDto();
        userDto.setUsername("zxcvb");
        userDto.setEmail("zxcvb@gmail.com");
        userDto.setPassword("zxcvbn");

    }

    @Test
    void findByUsernameTest() {
        user1.setUsername("username");
        when(userRepository.findByUsername("username")).thenReturn(user1);
        var user = userRepository.findByUsername("username");
        verify(userRepository, times(1)).findByUsername("username");
        assertEquals("username", user.getUsername());
    }

    @Test
    void findByUsernameFailTest() {
        when(userRepository.findByUsername("username")).thenReturn(null);
        var user = userRepository.findByUsername("username");
        verify(userRepository, times(1)).findByUsername("username");
        assertNull(user);
    }

    @Test
    void checkAndChangeVerificationStatusFailTest() {
        when(userRepository.findByVerificationCode("aaaaaaaa")).thenReturn(null);
        boolean isChanged = userService.checkAndChangeVerificationStatus("aaaaaaaa");
        assertFalse(isChanged);
    }

    @Test
    void checkAndChangeVerificationStatusTest() {
        when(userRepository.findByVerificationCode(user2.getVerificationCode())).thenReturn(user2);
        boolean isChanged = userService.checkAndChangeVerificationStatus("cvbnmzx");
        assertTrue(isChanged);
        assertTrue(user2.getVerificationStatus());
        assertNotNull(user2.getVerificationCode());
        verify(userRepository, times(1)).saveAndFlush(user2);
    }


    @Test
    void updatePasswordOfUserTest() throws ServiceException {
        var userChange = User.builder()
                .id(1)
                .username("user")
                .password("useruser")
                .name("user")
                .surname("user")
                .phoneNumber("375293579518")
                .email("user@gmail.com")
                .verificationStatus(true)
                .verificationCode(null)
                .build();
        boolean isChanged = userService.updatePasswordOfUser(user1, "useruser");
        assertTrue(isChanged);

    }

}
