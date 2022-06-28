package com.academy.car_rental.service;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    List<Payment> getAll();

    Payment getById(Integer id) throws ServiceException;

    void save(Payment payment);

    void delete(Integer id) throws ServiceException;

    /**
     * This method create new payment for accident
     *
     * @param order - order which necessary paid
     * @return payment
     */
    Payment createNewPayment(Order order);

    /**
     * This method create new payment for accident
     *
     * @param order     - order in which accident happened
     * @param totalCost - cost of car's damage
     * @return payment
     */
    Payment createNewPaymentForAccident(Order order, BigDecimal totalCost);

    List<Payment> getAllPaymentsByOrder(Order order);

    List<Payment> findByStartDate(String date);



}
