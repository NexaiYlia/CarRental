package com.academy.car_rental.controller;

import com.academy.car_rental.dto.UserDto;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.academy.car_rental.constant.Constants.ERROR_EXCEPTION_PAGE;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto1;


    @Test
    void authorizationGetTest() throws Exception {
        var dto = new UserDto();
        this.mockMvc.perform(get("/registration")
                        .flashAttr("user", dto))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("user", dto))
                .andExpect(view().name("registration"));
    }


    @Test
    @WithMockUser(username = "user")
    void authorizationPostWrongFailTest2() throws Exception {
        this.mockMvc.perform(post("/registration")
                        .flashAttr("user", userDto1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user")
    void makeVerificationTest() throws Exception {
        String code = "asldkasdlq";
        when(userService.checkAndChangeVerificationStatus(code)).thenReturn(true);
        this.mockMvc.perform(get("/verification")
                        .param("code", code))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));
        verify(userService, times(1)).checkAndChangeVerificationStatus(code);
    }

    @Test
    void loginPageTest() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(0))
                .andExpect(view().name("login"));
    }
}
