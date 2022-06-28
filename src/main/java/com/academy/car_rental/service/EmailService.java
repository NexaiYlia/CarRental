package com.academy.car_rental.service;

import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmailAboutCanceledOrderByAdmin(Order order, String comment) throws MessagingException, UnsupportedEncodingException;

    void sendEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;

    void sendEmailAboutChangeStatus(User user) throws MessagingException, UnsupportedEncodingException;
}
