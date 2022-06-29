package com.academy.car_rental.service.impl;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.type.CarType;
import com.academy.car_rental.model.repository.CarRepository;
import com.academy.car_rental.model.repository.OrderRepository;
import com.academy.car_rental.service.CarService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final OrderRepository orderRepository;


    public List<Car> getAll() {
        return carRepository.findAll();
    }

    @Override
    public Car getById(Integer id) throws ServiceException {
        Optional<Car> result = carRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ServiceException("Car not found");
    }

    @Override
    public void save(Car car) {
        carRepository.save(car);
    }

    @Override
    public boolean createNewCar(Car car, MultipartFile multipartFile) throws IOException, ServiceException {
        Car carNew = Car.builder()
                .brand(car.getBrand())
                .model(car.getModel())
                .manufacturedYear(car.getManufacturedYear())
                .engineType(car.getEngineType())
                .pricePerDay(car.getPricePerDay())
                .gearbox(car.getGearbox())
                .carType(car.getCarType())
                .build();
        save(carNew);
        var carUpdate = getById(carNew.getId());
        uploadFile(carUpdate, multipartFile);
        save(carUpdate);
        return true;
    }

    @Transactional
    public boolean saveUpdatedCar(Car car, Integer id, MultipartFile multipartFile) throws ServiceException, IOException {
        var updatedCar = getById(id);
        updatedCar.setCarType(car.getCarType());
        updatedCar.setModel(car.getModel());
        updatedCar.setBrand(car.getBrand());
        updatedCar.setGearbox(car.getGearbox());
        updatedCar.setCarType(car.getCarType());
        updatedCar.setManufacturedYear(car.getManufacturedYear());
        updatedCar.setEngineType(car.getEngineType());
        updatedCar.setPricePerDay(car.getPricePerDay());

        uploadFile(updatedCar, multipartFile);
        carRepository.saveAndFlush(updatedCar);
        return true;
    }

    @Override
    public boolean checkHasCarOrder(Integer carId) {
        var car = carRepository.findById(carId).get();
        List<Order> orders = orderRepository.findByCar(car);
        if (orders.isEmpty()) {
            return false;
        }
        return true;
    }


    @Override
    public void delete(Integer id) throws ServiceException {
        Long count = carRepository.countById(id);
        if (count == null || count == 0) {
            throw new ServiceException("Could not find any cars with id: " + id);

        }
        carRepository.deleteById(id);
    }

    @Override
    public List<Car> findCarsByPricePerDayBetween(BigDecimal pricePerDayAfter, BigDecimal pricePerDayBefore) {
        return carRepository.findCarsByPricePerDayBetween(pricePerDayAfter, pricePerDayBefore);
    }

    @Override
    public List<Car> findCarsByCarType(CarType carType) {
        return carRepository.findCarsByCarType(carType);
    }

    @Override
    public List<Car> findCarsByBrand(String brand) {
        return carRepository.findCarsByBrand(brand);
    }


    private boolean uploadFile(Car updatedCar, MultipartFile multipartFile) throws IOException {

        if (!multipartFile.getOriginalFilename().isBlank()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            updatedCar.setImg(fileName);
            String uploadDirectory = "./cars-images/" + updatedCar.getId();

            Path uploadPath = Paths.get(uploadDirectory);

            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                FileUtils.cleanDirectory(new File(uploadPath.toString()));
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save uploaded file:" + fileName, ioe);
            }
            return true;
        } else {
            return false;
        }
    }
}

