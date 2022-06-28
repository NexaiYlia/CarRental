package com.academy.car_rental.service.impl;

import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {
    public static final String APP_MAIL = "nexai.ylia@gmail.com";
    public static final String SENDER_NAME = "Car rental";
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailAboutCanceledOrderByAdmin(Order order, String comment) throws MessagingException, UnsupportedEncodingException {
        String toAddress = order.getUser().getEmail();
        String subject = "Your order was canceled";
        String content = "Dear [[name]] [[surname]], <br>"
                + "The order number [[id]]  was canceled: [[comment]] <br>"
                + "Thank you<br>" +
                "Car rental.";
        content = content.replace("[[name]]", order.getUser().getName());
        content = content.replace("[[surname]]", order.getUser().getSurname());
        content = content.replace("[[id]]", order.getId().toString());
        content = content.replace("[[comment]]", comment);


        createMessageAndSendIt(toAddress, subject, content);
    }


    public void sendEmailAboutChangeStatus(User user) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String subject = "Your status was changed to administrator";
        String content = "Dear [[name]] [[surname]], <br>"
                + "welcome to the \"Rent a car\" team. Now you have access to new features when working with the application. " +
                "But unfortunately you will not be able to place an order for renting a car under this username."
                + "Thank you<br>" +
                "Car rental.";
        content = content.replace("[[name]]", user.getName());
        content = content.replace("[[surname]]", user.getSurname());

        createMessageAndSendIt(toAddress, subject, content);
    }


    private void createMessageAndSendIt(String toAddress, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(APP_MAIL, SENDER_NAME);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }



    @Override
    public void sendEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String subject = "Please verify your registration";
        String content = "Dear [[username]], <br>"
                + "Please click the link below to verify your registration: <br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you<br>" +
                "Car rental.";
        content = content.replace("[[username]]", user.getUsername());
        String verifyURL = siteURL + "/verification?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        createMessageAndSendIt(toAddress, subject, content);
    }
}
