package com.academy.car_rental.dto;

import com.academy.car_rental.model.entity.type.Role;
import com.academy.car_rental.validator.PhoneNumberConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "username is required field")
    @Size(min = 3, message = "username must be more than 3 symbols")
    private String username;
    @NotBlank(message = "password is required field")
    @Size(min = 6, message = "password must be more than 6 symbols")
    private String password;

    @Size(min = 2, message = "firstName must be min 2 symbols ")
    @Size(max = 30, message = "firstName must be max 30 symbols")
    @NotBlank(message = "First name is required field")
    private String name;

    @Size(min = 2, message = "firstName must be min 2 symbols ")
    @Size(max = 30, message = "firstName must be max 30 symbols")
    @NotBlank(message = "First name is required field")
    private String surname;
    @PhoneNumberConstraint
    private String phoneNumber;
    @Email
    @NotBlank(message = "Email is required field")
    private String email;
    private Boolean verificationStatus;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

}



