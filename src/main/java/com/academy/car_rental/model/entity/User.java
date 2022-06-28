package com.academy.car_rental.model.entity;

import com.academy.car_rental.model.entity.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    @NotBlank(message = "username is required field")
    @Size(min = 4, message = "name must be min 6 symbols")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "password is required field")
    @Size(min = 4, message = "password must be min 6 symbols")
    private String password;

    @Column(name = "first_name")
    private String name;

    @Column(name = "second_name")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_status")
    private Boolean verificationStatus;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;


}

