package com.academy.car_rental.controller;

import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainControllerTest {

    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @MockBean
    UserController userController;
    @MockBean
    private Principal principal;

    @MockBean
    private UserService userService;


    private User user;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        user = User.builder()
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
    }

    @Test
    @WithMockUser(username = "qwerty")
    void mainPageWithUser() throws Exception {
        when(principal.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(user);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(view().name("index"));
    }
}
