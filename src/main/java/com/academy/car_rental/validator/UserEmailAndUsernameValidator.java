package com.academy.car_rental.validator;

import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.model.entity.User;

public interface UserEmailAndUsernameValidator {

    String validateEmail(UserDto userDto);

    public String validateUsername(UserDto userDto);
}
