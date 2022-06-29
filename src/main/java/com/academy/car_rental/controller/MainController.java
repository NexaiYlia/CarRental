package com.academy.car_rental.controller;

import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.type.CarType;
import com.academy.car_rental.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

import static com.academy.car_rental.constant.Constants.CARS_FOR_MODEL;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CarService carService;

    @GetMapping(value = "/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping(value = "/about")
    public String showAboutPage() {
        return "about";

    }
    @GetMapping(value = "/terms")
    public String showTermsPage() {
        return "terms";

    }

    @GetMapping(value = "/contacts")
    public String showContactsPage() {
        return "contacts";

    }

    @GetMapping(value = "/carsForUser")
    public String showCarsByPrice(@RequestParam BigDecimal pricePerDayAfter, @RequestParam BigDecimal pricePerDayBefore, Model model) {
        List<Car> cars = carService.findCarsByPricePerDayBetween(pricePerDayAfter, pricePerDayBefore);
        model.addAttribute(CARS_FOR_MODEL, cars);
        return "car/carsForUser";
    }

    @GetMapping(value = "/carsForUser/type")
    public String showCarsByType(@RequestParam CarType carType, Model model) {
        List<Car> cars = carService.findCarsByCarType(carType);
        model.addAttribute(CARS_FOR_MODEL, cars);
        return "car/carsForUser";
    }

    @GetMapping(value = "/carsForUser/brand")
    public String showCarsByType(@RequestParam String brand, Model model) {
        List<Car> cars = carService.findCarsByBrand(brand);
        model.addAttribute(CARS_FOR_MODEL, cars);
        return "car/carsForUser";
    }
}
