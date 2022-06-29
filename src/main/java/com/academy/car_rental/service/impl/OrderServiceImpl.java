package com.academy.car_rental.service.impl;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.model.entity.type.OrderStatus;
import com.academy.car_rental.model.repository.AccidentRepository;
import com.academy.car_rental.model.repository.CarRepository;
import com.academy.car_rental.model.repository.OrderRepository;
import com.academy.car_rental.model.repository.UserRepository;
import com.academy.car_rental.service.EmailService;
import com.academy.car_rental.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static com.academy.car_rental.constant.Constants.ASC;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final AccidentRepository accidentRepository;
    private final EmailService emailService;

    @Override
    public Page<Order> findAll(int pageNumber, String sortField, String sortDirection) throws ServiceException {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);
        var pageOFOrder = orderRepository.findAll(pageable);
        if (pageOFOrder.getContent().isEmpty()) {
            throw new ServiceException("Wrong page");
        }
        return pageOFOrder;
    }

    @Override
    public Page<Order> findAllByStatus(int pageNumber, String sortField, String sortDirection, OrderStatus status) throws ServiceException {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals(ASC) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);
        var pageOFOrder = orderRepository.findAllByStatus(pageable, status);
        if (pageOFOrder.getContent().isEmpty()) {
            throw new ServiceException("Wrong page");
        }
        return pageOFOrder;
    }


    @Override
    public Page<Order> findAllByStartDate(int pageNumber, String sortField, String sortDirection, String date) throws ServiceException {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals(ASC) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);
        LocalDate startDate = LocalDate.parse(date);
        var pageOFOrder = orderRepository.findAllByStartDate(pageable, startDate);
        if (pageOFOrder.getContent().isEmpty()) {
            throw new ServiceException("Wrong page");
        }
        return pageOFOrder;
    }


    @Override
    public Page<Order> findByUserNameOrSurname(int pageNumber, String sortField, String sortDirection, String name) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals(sortField) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 5, sort);
        var pageOFOrder = orderRepository.findAllByUserName(pageable, name);
        if (pageOFOrder.isEmpty()) {
            pageOFOrder = orderRepository.findAllByUserSurname(pageable, name);
        }
        return pageOFOrder;
    }


    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order getById(Integer id) throws ServiceException {
        Optional<Order> result = orderRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ServiceException("Order not found");
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void delete(Integer id) throws ServiceException {
        Long count = orderRepository.countById(id);
        if (count == null || count == 0) {
            throw new ServiceException("Could not find any orders with id: " + id);

        }
        orderRepository.deleteById(id);
    }


    @Override
    public BigDecimal countCost(Car car, String startDay, String endDay) {
        BigDecimal cost;
        LocalDate start = LocalDate.parse(startDay);
        LocalDate end = LocalDate.parse(endDay);
        int countDay = countOrderDayAmount(start, end);
        BigDecimal price = car.getPricePerDay();
        cost = price.multiply(BigDecimal.valueOf(countDay));

        return cost;
    }


    @Override
    public boolean existsByDateOfOrder(Integer carId, LocalDate startDay, LocalDate endDay) {
        List<Order> orders = orderRepository.findAll();
        if (!orders.isEmpty()) {
            for (Order order : orders) {
                if (order.getCar().getId() == carId && order.getStartDate().isBefore(endDay) && order.getEndDate().isAfter(startDay)) {
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public List<Order> findByStatus(OrderStatus orderStatus) {
        List<Order> orders = orderRepository.findByStatus(orderStatus);
        return orders;
    }

    @Override
    public List<Order> findByStartDate(String date) {
        LocalDate startDate = LocalDate.parse(date);
        List<Order> orders = orderRepository.findByStartDate(startDate);
        return orders;
    }

    @Override
    public boolean changeOrderStatus(Integer id) {
        Order order = orderRepository.getById(id);
        if (order.getStatus().equals(OrderStatus.CANCELED) || order.getStatus().equals(OrderStatus.CANCELED_BY_USER)) {
            return false;
        }
        List<Accident> accidents = accidentRepository.findAllAccidentsByOrder(order);
        for (Accident entry : accidents) {
            if (!entry.isPaid()) {
                return false;
            }
        }
        order.setStatus(OrderStatus.APPROVED);
        orderRepository.save(order);
        return true;
    }

    private int countOrderDayAmount(LocalDate startDay, LocalDate endDay) {
        Period period = Period.between(startDay, endDay);
        return period.getDays();
    }

    @Override
    @Transactional
    public boolean createNewOrder(Integer carId, String startDay, String endDay, Principal principal) {
        LocalDate start = LocalDate.parse(startDay);
        LocalDate end = LocalDate.parse(endDay);
        if (start.isAfter(end) || start.isBefore(LocalDate.now())) {
            return false;
        }
        if (existsByDateOfOrder(carId, start, end)) {
            return false;
        }
        User user = userRepository.findByUsername(principal.getName());
        Car car = carRepository.getById(carId);
        BigDecimal cost = countCost(car, startDay, endDay);

        Order order = Order.builder()
                .user(user)
                .car(car)
                .startDate(start)
                .endDate(end)
                .status(OrderStatus.NEW)
                .cost(cost)
                .build();
        orderRepository.save(order);
        return true;
    }

    @Override
    public Order showNewOrder(Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        List<Order> orders = orderRepository.findByUser(user, Sort.by("id").descending());
        return orders.get(0);
    }

    @Override
    public boolean cancelOrderByAdmin(Integer id, String comment) throws MessagingException, UnsupportedEncodingException {
        Order order = orderRepository.getById(id);
        if (order.getStatus().equals(OrderStatus.CANCELED) || order.getStatus().equals(OrderStatus.CANCELED_BY_USER)) {
            return false;
        }
        order.setStatus(OrderStatus.CANCELED);
        order.setStartDate(null);
        order.setEndDate(null);
        orderRepository.save(order);
        emailService.sendEmailAboutCanceledOrderByAdmin(order, comment);
        return true;
    }

    @Override
    public boolean cancelOrderByUser(Integer id) {
        Order order = orderRepository.getById(id);
        if (order.getStatus().equals(OrderStatus.CANCELED) || order.getStatus().equals(OrderStatus.CANCELED_BY_USER)) {
            return false;
        }
        order.setStatus(OrderStatus.CANCELED_BY_USER);
        order.setStartDate(null);
        order.setEndDate(null);
        orderRepository.save(order);
        return true;
    }

    @Override
    public List<Order> getOrderByUserId(User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders;
    }

    @Override
    public boolean isOrderActive(Order order) {
        if (order.getStatus().equals(OrderStatus.CANCELED) || order.getStatus().equals(OrderStatus.CANCELED_BY_USER)) {
            return false;
        }
        return true;
    }

    public List<Order> findByUser(User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders;
    }


    @Override
    public List<Order> findByUserNameOrSurname(String name) {
        List<Order> orders = orderRepository.findByUserName(name);
        if (orders.isEmpty()) {
            orders = orderRepository.findByUserSurname(name);
        }
        return orders;
    }

}
