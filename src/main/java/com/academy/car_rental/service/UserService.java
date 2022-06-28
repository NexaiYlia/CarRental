package com.academy.car_rental.service;

import com.academy.car_rental.dto.UserDto;
import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.User;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<User> findAll(int pageNumber, String sortField, String sortDirection) throws ServiceException;

    List<User> getAll();

    User getById(Integer id) throws ServiceException;

    void save(UserDto userDto) throws MessagingException, UnsupportedEncodingException;

    void delete(Integer id) throws ServiceException;

    User findByVerificationCode(String verificationCode);

    boolean checkAndChangeVerificationStatus(String code);

    /**
     * This method find all orders of user with username
     *
     * @param username -  username of user whose orders need to find.
     * @return if the user is found then returns User
     */
    User findByUsername(String username);

    /**
     * This method find all orders of user with email
     *
     * @param email -  email of user whose orders need  to find.
     * @return if the user is found then returns User
     */
    User findByEmail(String email);

    /**
     * This method checks changing of  username, name,phone number, email and makes these changes and saves a user.
     *
     * @param userDto - a userDto with changes.
     */
    void update(UserDto userDto) throws MessagingException, UnsupportedEncodingException;

    /**
     * This method checks if the user has orders
     *
     * @param userId -  id of user whose orders to find.
     * @return If the method has completed  return true
     */
    boolean checkHasUserOrder(Integer userId);

    void makeAdmin(User user) throws MessagingException, UnsupportedEncodingException;

    boolean updatePasswordOfUser(Integer userId, String password) throws ServiceException;
}
