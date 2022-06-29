package com.academy.car_rental.service;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface AccidentService {
    List<Accident> getAll();

    Accident getById(Integer id) throws ServiceException;


    void save(Accident accident);

    void delete(Integer id);

    /**
     * This method finds all accidents in this order.
     *
     * @param order - selected order
     * @return accidents - list of this order's accident
     */
    List<Accident> getAllAccidentsByOrder(Order order);

    void changeAccidentPaymentStatus(Order order);

    /**
     * This method  creates a new accident  and saves it.
     *
     * @param cost        - cost of accident
     * @param description - description of accident
     * @param orderId     - id of the order in which the incident happened
     * @param accident    -  accident with new data
     * @return If the method is successful, true is returned
     */
    boolean createNewAccident(Accident accident, BigDecimal cost, String description, Integer orderId);
}
