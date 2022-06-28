package com.academy.car_rental.model.repository;

import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;
import com.academy.car_rental.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Long countById(Integer id);

    List<Payment> findAllPaymentByOrder(Order order);

    List<Payment> findByPaymentDate(LocalDate startDate);



}
