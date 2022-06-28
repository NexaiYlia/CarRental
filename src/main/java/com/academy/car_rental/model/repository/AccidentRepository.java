package com.academy.car_rental.model.repository;

import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccidentRepository extends JpaRepository<Accident, Integer> {
    List<Accident> findAllAccidentsByOrder(Order order);
}
