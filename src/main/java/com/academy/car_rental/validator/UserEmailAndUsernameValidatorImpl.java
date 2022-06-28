package com.academy.car_rental.validator;


import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEmailAndUsernameValidatorImpl implements UserEmailAndUsernameValidator {
    private final UserService userService;


    @Override
    public String validateEmail(UserDto userDto) {
        String message = null;
        var user = userService.findByEmail(userDto.getEmail());
        if ((user != null && !user.getUsername().equals(userDto.getUsername())))
            message = "Email also exists";
        return message;
    }

    @Override
    public String validateUsername(UserDto userDto) {
        String message = null;
        var user = userService.findByUsername(userDto.getEmail());
        if (user != null)
            message = "User with this username also exists";
        return message;
    }
}

