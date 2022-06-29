package com.academy.car_rental.controller;

import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.service.UserService;
import com.academy.car_rental.validator.UserEmailAndUsernameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

import static com.academy.car_rental.constant.Constants.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserEmailAndUsernameValidator userEmailAndUsernameValidator;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{pageNumber}")
    public String userListByPage(Model model,
                                 @PathVariable("pageNumber") int pageNumber,
                                 @RequestParam("sortField") String sortField,
                                 @RequestParam("sortDirection") String sortDirection) throws ServiceException {

        var page = userService.findAll(pageNumber, sortField, sortDirection);
        var users = page.getContent();
        model.addAttribute(PAGE_FOR_MODEL, page);
        model.addAttribute(USERS_FOR_MODEL, users);
        model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);
        String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
        model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
        model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);

        return "user/users";
    }

    @GetMapping(value = "/new")
    public String newUser(Model model) {
        model.addAttribute(USER_FOR_MODEL, new UserDto());
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new user");

        return "user/user_form";
    }

    @PostMapping(value = "/save")
    public String createUser(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult bindingResult, RedirectAttributes ra) throws MessagingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            return "user/user_form";
        }
        userService.save(userDto);
        ra.addFlashAttribute(MESSAGE, "The user has been saved successfully.");
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }


    @PostMapping(value = "/update")
    public String saveUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, RedirectAttributes ra) throws MessagingException, UnsupportedEncodingException {
        var message = userEmailAndUsernameValidator.validateEmail(userDto);
        if (message != null) {
            ObjectError error = new ObjectError(EMAIL, message);
            bindingResult.addError(error);
        }
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        userService.update(userDto);
        ra.addFlashAttribute(MESSAGE, "The user has been edited successfully.");
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }


    @GetMapping(value = "/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            if (!userService.checkHasUserOrder(id)) {
                userService.delete(id);
                ra.addFlashAttribute(MESSAGE, "The user was deleted successfully");
            } else {
                ra.addFlashAttribute(MESSAGE, "It is not possible to delete the user because there are active orders");
            }
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

        }
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }

    @GetMapping(value = "/makeAdmin/{id}")
    public String makeAdmin(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            var user = userService.getById(id);
            userService.makeAdmin(user);
            ra.addFlashAttribute(MESSAGE, "User status has been changed to administrator.");

        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

        } catch (MessagingException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

        } catch (UnsupportedEncodingException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

        }
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }

    @GetMapping(value = "/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            User user = userService.getById(id);
            model.addAttribute(USER_FOR_MODEL, user);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit user (id: " + id + ")");
            return "user/edit";
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The user wasn't found." + e.getMessage());
            return "user/users";
        }
    }

    @PreAuthorize(("isAuthenticated()"))
    @GetMapping(value = "/userProfile")
    public String userInfo(Principal principal, Model model) {
        var user = userService.findByUsername(principal.getName());
        model.addAttribute(USER_FOR_MODEL, user);
        return "user/user_profile";
    }

    @PostMapping("/registration/byAdmin")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult bindingResult,
                               RedirectAttributes ra) throws MessagingException, UnsupportedEncodingException {
        var emailMessage = userEmailAndUsernameValidator.validateEmail(user);
        if (emailMessage != null) {
            ObjectError error = new ObjectError(EMAIL, emailMessage);
            bindingResult.addError(error);
        }
        var usernameMessage = userEmailAndUsernameValidator.validateUsername(user);
        if (usernameMessage != null) {
            ObjectError error = new ObjectError(USERNAME, usernameMessage);
            bindingResult.addError(error);
        }
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        userService.save(user);
        ra.addFlashAttribute(MESSAGE, "The user was added successfully.");
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/updatePasswordOfUser/{id}")
    public String updatePasswordOfUser(@PathVariable("id") Integer userId,
                                       @RequestParam("password") String password,
                                       RedirectAttributes ra) throws ServiceException {
        userService.updatePasswordOfUser(userId, password);
        ra.addFlashAttribute(MESSAGE, "Password changed successfully");
        return "redirect:/users/userProfile";

    }
}
