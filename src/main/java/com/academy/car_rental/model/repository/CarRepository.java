package com.academy.car_rental.model.repository;

import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.type.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    long countById(Integer id);

    List<Car> findAll();

    List<Car> findCarsByPricePerDayBetween(BigDecimal pricePerDayAfter, BigDecimal pricePerDayBefore);

    List<Car> findCarsByCarType(CarType carType);
    List<Car> findCarsByBrand(String brand);


}
