package com.academy.car_rental.controller;

import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.exception.UserException;
import com.academy.car_rental.service.UserService;
import com.academy.car_rental.validator.UserEmailAndUsernameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

import static com.academy.car_rental.constant.Constants.*;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final UserEmailAndUsernameValidator userEmailAndUsernameValidator;

    @GetMapping("/registration")
    public String registration(@ModelAttribute("userDto") UserDto userDto) {
        return "registration";

    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userDto") @Valid UserDto userDto,
                               BindingResult bindingResult) throws MessagingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        var emailMessage = userEmailAndUsernameValidator.validateEmail(userDto);
        if (emailMessage != null) {
            ObjectError error = new ObjectError(EMAIL, emailMessage);
            bindingResult.addError(error);
        }
        var usernameMessage = userEmailAndUsernameValidator.validateUsername(userDto);
        if (usernameMessage != null) {
            ObjectError error = new ObjectError(USERNAME, usernameMessage);
            bindingResult.addError(error);
        }
        userService.save(userDto);
        return "redirect:/";
    }


    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (request.getParameterMap().get(ERROR_FOR_MODEL) != null)
                model.addAttribute(ERROR_FOR_MODEL, "You entered wrong parameters. Please try again");
            return "login";
        } else {
            return "/";
        }
    }

    @GetMapping("/verification")
    public String makeVerification(@RequestParam("code") String verificationCode, Principal principal) throws UserException {

        if (!userService.checkAndChangeVerificationStatus(verificationCode)) {
            throw new UserException("There is no user with the code :" + verificationCode);
        } else {
            if (principal != null) {
                return "redirect:/logout";
            } else {
                return "redirect:/";
            }
        }
    }

}

