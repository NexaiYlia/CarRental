package com.academy.car_rental.model.repository;

import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.model.entity.type.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {


    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByStatus(Pageable pageable, OrderStatus status);

    Page<Order> findAllByStartDate(Pageable pageable, LocalDate startDate);

    Page<Order> findAllByUserName(Pageable pageable, String name);

    Page<Order> findAllByUserSurname(Pageable pageable, String surname);

    List<Order> findByCar(Car car);

    List<Order> findByUser(User user, Sort sort);

    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStartDate(LocalDate startDate);

    List<Order> findAll();

    Order save(Order order);

    Long countById(Integer id);

    List<Order> findByUserName(String name);

    List<Order> findByUserSurname(String surname);
}
