package com.academy.car_rental.service;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.type.CarType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface CarService {

    public List<Car> getAll();

    Car getById(Integer id) throws ServiceException;

    void save(Car car);

    void delete(Integer id) throws ServiceException;

    /**
     * This method checks changing of a model, brand, img , price per day, car type and makes these changes and saves a car.
     *
     * @param car           - car which will be updated
     * @param id            - id of car
     * @param multipartFile - an img of car.
     * @return true
     * @throws ServiceException If there is no car with car.getId()
     * @throws IOException      if there are some problems with updating image
     */
    boolean saveUpdatedCar(Car car, Integer id, MultipartFile multipartFile) throws IOException, ServiceException;


    /**
     * This method find cars with price per day between introduced prices
     *
     * @param pricePerDayAfter  - lower price limit
     * @param pricePerDayBefore - upper price limit
     * @return cars -list of car which price is between pricePerDayAfter and pricePerDayAfter
     */
    List<Car> findCarsByPricePerDayBetween(BigDecimal pricePerDayAfter, BigDecimal pricePerDayBefore);

    /**
     * This method find cars with introduced car type
     *
     * @param carType  - necessary car type
     * @return cars -list of car which have introduced car type
     */
    List<Car> findCarsByCarType(CarType carType);

    boolean createNewCar(Car car, MultipartFile multipartFile) throws IOException, ServiceException;


    /**
     * This method checks does the car have orders
     *
     * @param carId  - id of selected car
     * @return true if car has orders
     */
    boolean checkHasCarOrder(Integer carId);

   List<Car> findCarsByBrand(String brand);


}
