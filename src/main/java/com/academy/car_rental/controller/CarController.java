package com.academy.car_rental.controller;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.academy.car_rental.constant.Constants.*;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public String showCarList(Model model) {
        List<Car> cars = carService.getAll();
        model.addAttribute(CARS_FOR_MODEL, cars);

        return "car/cars";
    }

    @GetMapping(value = "/carsForUser")
    public String showCarForUser(Model model) {
        List<Car> cars = carService.getAll();
        model.addAttribute(CARS_FOR_MODEL, cars);
        return "car/carsForUser";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/new")
    public String showNewForm(@ModelAttribute("car") Car car) {
//        model.addAttribute(CAR_FOR_MODEL, new Car());
//        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new car");

        return "car/car_form";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/save")
    public String saveCar(@ModelAttribute("car") @Valid Car car, BindingResult bindingResult, RedirectAttributes ra, @RequestParam("carImg") MultipartFile multipartFile) throws ServiceException, IOException {

        if (bindingResult.hasErrors()) {
            return "car/car_form";
        }
        carService.createNewCar(car, multipartFile);
        ra.addFlashAttribute(MESSAGE, "The car has been saved successfully.");
        return "redirect:/cars";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/update")
    public String updateCar(@RequestParam("id") Integer id, @Valid Car car, BindingResult bindingResult, RedirectAttributes ra, @RequestParam("carImg") MultipartFile multipartFile) throws ServiceException, IOException {
        if (bindingResult.hasErrors()) {
            car.setImg(carService.getById(car.getId()).getImg());
            return "car/edit";
        }
        carService.saveUpdatedCar(car, id, multipartFile);

        ra.addFlashAttribute(MESSAGE, "The car has been saved successfully.");
        return "redirect:/cars";
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/delete/{id}")
    public String deleteCar(@PathVariable("id") Integer id, RedirectAttributes ra) {

        try {
            if (!carService.checkHasCarOrder(id)) {
                carService.delete(id);
                ra.addFlashAttribute(MESSAGE, "The car because was deleted successfully");
            } else {
                ra.addFlashAttribute(MESSAGE, "It is not possible to delete the car because there are active orders");

            }

        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());
        }

        return "redirect:/cars";

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/edit/{id}")
    public String showEditCarForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Car car = carService.getById(id);
            model.addAttribute(CAR_FOR_MODEL, car);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit car (id: " + id + ")");
            return "car/edit";
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The car wasn't found." + e.getMessage());
            return "redirect:car/cars";
        }
    }

    @GetMapping(value = "/rent/{id}")
    public String rentCar(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            var car = carService.getById(id);
            model.addAttribute(CAR_FOR_MODEL, car);
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

        }
        return "order/order";

    }

    @GetMapping(value = "/details/{id}")
    public String showCarDetailsForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Car car = carService.getById(id);
            model.addAttribute(CAR_FOR_MODEL, car);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Car details");
            return "car/car";
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The car wasn't found." + e.getMessage());
            return "car/carsForUser";
        }
    }
}
