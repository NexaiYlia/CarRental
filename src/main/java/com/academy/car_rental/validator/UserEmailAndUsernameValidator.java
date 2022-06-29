package com.academy.car_rental.validator;

import com.academy.car_rental.dto.UserDto;

public interface UserEmailAndUsernameValidator {

    String validateEmail(UserDto userDto);

    public String validateUsername(UserDto userDto);
}
