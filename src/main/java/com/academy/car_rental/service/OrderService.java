package com.academy.car_rental.service;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.model.entity.type.OrderStatus;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    List<Order> getAll();

    Order getById(Integer id) throws ServiceException;

    void save(Order order);

    void delete(Integer id) throws ServiceException;

    List<Order> findByUser(User user);

    /**
     * This method calculates the cost of renting a car in a specified period of time.
     *
     * @param car      - machine for which the cost is calculated
     * @param startDay - first day of rental
     * @param endDay   - last day of rental
     * @return cost of rental
     */
    BigDecimal countCost(Car car, String startDay, String endDay);


    /**
     * This method checks if the car is booked for the selected dates
     *
     * @param carId    - id of selected car
     * @param startDay - first day of rental
     * @param endDay   - last day of rental
     * @return true if the car is busy
     */
    boolean existsByDateOfOrder(Integer carId, LocalDate startDay, LocalDate endDay);

    /**
     * This method creates new order with entered parameters
     *
     * @param carId     - id of selected car
     * @param startDay  - first day of rental
     * @param endDay    - last day of rental
     * @param principal - user who make this order
     * @return true value if the method has completed
     */
    boolean createNewOrder(Integer carId, String startDay, String endDay, Principal principal);

    Order showNewOrder(Principal principal);

    /**
     * This method changes order status for APPROVED
     *
     * @param id - id of selected order
     * @return true value if the method has completed
     */
    boolean changeOrderStatus(Integer id);

    /**
     * This method cancel order by admin
     *
     * @param id      - id of selected order
     * @param comment - the reason why the order was canceled
     * @return true value if the method has completed
     * @throws MessagingException           if there are some problems with sending a message
     * @throws UnsupportedEncodingException if there are some problems with sending a message
     */
    boolean cancelOrderByAdmin(Integer id, String comment) throws MessagingException, UnsupportedEncodingException;

    /**
     * This method cancel order by user
     *
     * @param id - id of selected order
     * @return true value if the method has completed
     */
    boolean cancelOrderByUser(Integer id);

    List<Order> getOrderByUserId(User user);

    /**
     * This method  checks if the order is still active
     *
     * @param order - selected order
     * @return true value if the method has completed
     */
    boolean isOrderActive(Order order);

    /**
     * This method  checks if the order is still active
     *
     * @param orderStatus - selected orderStatus
     * @return orders - list of orders with this status
     */
    List<Order> findByStatus(OrderStatus orderStatus);

    /**
     * This method  finds all orders with specified status
     *
     * @param date - start date of order
     * @return orders - list of orders with this start date
     */
    List<Order> findByStartDate(String date);

    List<Order> findByUserNameOrSurname(String name);

    Page<Order> findAll(int pageNumber, String sortField, String sortDirection) throws ServiceException;

    Page<Order> findAllByStatus(int pageNumber, String sortField, String sortDirection, OrderStatus status) throws ServiceException;

    Page<Order> findAllByStartDate(int pageNumber, String sortField, String sortDirection, String date) throws ServiceException;

    Page<Order> findByUserNameOrSurname(int pageNumber, String sortField, String sortDirection, String name);
}
