package com.academy.car_rental.aop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

import static com.academy.car_rental.constant.Constants.*;


@ControllerAdvice
public class ProjectExceptionHandler {
    private static final Logger logger = LogManager.getLogger(ProjectExceptionHandler.class);


    @ExceptionHandler(value = Exception.class)
    protected ModelAndView defaultExceptionHandler(HttpServletRequest request, Exception exception, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        if (exception.getClass().getSimpleName().equals("AccessDeniedException") && principal == null) {
            modelAndView.setViewName(LOGIN_PAGE);
        } else {
            modelAndView.addObject(ERROR_FOR_MODEL, exception.getMessage());
            modelAndView.addObject(EXCEPTION_NAME_FOR_MODEL, exception.getClass().getSimpleName());
            modelAndView.setViewName(ERROR_EXCEPTION_PAGE);
        }
        logger.error(HANDLER_MESSAGE,
                exception.getClass().getSimpleName(),
                principal != null ? principal.getName() : PRINCIPAL_WITHOUT_USER,
                request.getRequestURL());
        return modelAndView;
    }

}
