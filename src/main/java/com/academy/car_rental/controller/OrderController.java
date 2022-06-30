package com.academy.car_rental.controller;


import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;
import com.academy.car_rental.model.entity.type.OrderStatus;
import com.academy.car_rental.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static com.academy.car_rental.constant.Constants.*;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final AccidentService accidentService;
    private final PaymentService paymentService;

    private final UserService userService;
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/{pageNumber}")
    public String showOrderList(Model model,
                                @PathVariable("pageNumber") int pageNumber,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDirection") String sortDirection) throws ServiceException {

        var page = orderService.findAll(pageNumber, sortField, sortDirection);
        var orders = page.getContent();

        List<Accident> accidents = accidentService.getAll();
        List<Payment> payments = paymentService.getAll();
        model.addAttribute(PAGE_FOR_MODEL, page);
        model.addAttribute(PAYMENTS_FOR_MODEL, payments);
        model.addAttribute(ORDERS_FOR_MODEL, orders);
        model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);
        model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);

        String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
        model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
        model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);

        return "order/orders";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/searchOrderByStatus/{pageNumber}")
    public String searchOrderByStatus(Model model,
                                      @RequestParam("status") OrderStatus status,
                                      @PathVariable("pageNumber") int pageNumber,
                                      @RequestParam("sortField") String sortField,
                                      @RequestParam("sortDirection") String sortDirection) throws ServiceException {
        var page = orderService.findAllByStatus(pageNumber, sortField, sortDirection, status);
        List<Order> orders = orderService.findByStatus(status);
        List<Accident> accidents = accidentService.getAll();
        List<Payment> payments = paymentService.getAll();
        model.addAttribute(PAGE_FOR_MODEL, page);
        model.addAttribute(ORDERS_FOR_MODEL, orders);
        model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);
        model.addAttribute(PAYMENTS_FOR_MODEL, payments);
        model.addAttribute(SORT_FIELD_FOR_MODEL, ID);

        String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
        model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
        model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);
        return "order/orders";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/searchOrderByDate/{pageNumber}")
    public String searchOrderByStatus(Model model,
                                      @RequestParam("date") String date,
                                      @PathVariable("pageNumber") int pageNumber,
                                      @RequestParam("sortField") String sortField,
                                      @RequestParam("sortDirection") String sortDirection) throws ServiceException {
        var page = orderService.findAllByStartDate(pageNumber, sortField, sortDirection, date);
        List<Order> orders = orderService.findByStartDate(date);
        List<Accident> accidents = accidentService.getAll();
        List<Payment> payments = paymentService.getAll();
        if (orders.isEmpty()) {

            model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);
            model.addAttribute(PAYMENTS_FOR_MODEL, payments);
            return "redirect:orders/1?sortField=id&sortDirection=asc";
        }

        model.addAttribute(PAGE_FOR_MODEL, page);
        model.addAttribute(ORDERS_FOR_MODEL, orders);
        model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);
        model.addAttribute(PAYMENTS_FOR_MODEL, payments);
        model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);
        String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
        model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
        model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);
        return "order/orders";
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/orders/cost/{id}")
    public String calculateCost(@PathVariable("id") Integer carId,
                                @RequestParam("startDate") String startDate,
                                @RequestParam("endDate") String endDate,
                                Model model,
                                Principal principal,
                                RedirectAttributes ra) throws ServiceException {
        var car = carService.getById(carId);

        if (startDate.isBlank() || endDate.isBlank()) {
            model.addAttribute(CAR_FOR_MODEL, car);
            model.addAttribute(TIME_FOR_MODEL, "*Please enter a valid date");
            return "order/order";
        }

        BigDecimal cost = orderService.countCost(car, startDate, endDate);
        model.addAttribute(START_DATE_FOR_MODEL, startDate);
        model.addAttribute(END_DATE_FOR_MODEL, endDate);
        model.addAttribute(CAR_FOR_MODEL, car);
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Create order");
        model.addAttribute(COST_FOR_MODEL, cost);

        return "order/order";
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/orders/new/{id}")
    public String createNewForm(@PathVariable("id") Integer carId,
                                @RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate,
                                Model model,
                                Principal principal,
                                RedirectAttributes ra) throws ServiceException {
        var car = carService.getById(carId);
        if (startDate.isBlank() || endDate.isBlank()) {
            model.addAttribute(CAR_FOR_MODEL, car);
            model.addAttribute(TIME_FOR_MODEL, "*Please enter a valid date");
            return "order/order";
        }

        if (orderService.createNewOrder(carId, startDate, endDate, principal)) {
            return "redirect:/orders/created";
        }
        model.addAttribute(MESSAGE, "On the dates you specified, the car is busy.Please select another date. ");
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Create order");
        model.addAttribute(CAR_FOR_MODEL, car);
        return "order/order";
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/orders/created")
    public String showCreatedOrder(Principal principal, Model model, RedirectAttributes ra) {
        Order order = orderService.showNewOrder(principal);
        var car = order.getCar();
        model.addAttribute(CAR_FOR_MODEL, car);
        model.addAttribute(ORDER_FOR_MODEL, order);
        return "order/created";
    }


    @PostMapping(value = "/orders/save")
    public String saveOrder(Order order, RedirectAttributes ra) {
        orderService.save(order);
        ra.addFlashAttribute(MESSAGE, "The order has been saved successfully.");
        return "redirect:order/orders/1?sortField=id&sortDirection=asc";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute(MESSAGE, "The order was deleted successfully.");
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());

        }
        return "redirect:/orders/1?sortField=id&sortDirection=asc";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/edit/{id}")
    public String showEditOrderForm(@PathVariable("id") Integer id, Model model, Principal
            principal, RedirectAttributes ra) {
        try {
            var user = userService.findByUsername(principal.getName());
            Order order = orderService.getById(id);
            model.addAttribute(ORDER_FOR_MODEL, order);
            model.addAttribute(USER_FOR_MODEL, user);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit order (id: " + id + ")");
            return "order/order";
        } catch (ServiceException e) {
            return "redirect:/orders/1?sortField=id&sortDirection=asc";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/changeStatus/{id}")
    public String changeOrderStatus(@PathVariable("id") Integer id, Model model, Principal
            principal, RedirectAttributes ra) {
        boolean result = orderService.changeOrderStatus(id);
        if (!result) {
            ra.addFlashAttribute(MESSAGE, "The status of the order cannot be changed because the order has been cancelled or not paid yet.");
        } else {
            ra.addFlashAttribute(MESSAGE, "The status of the order was changed successfully.");
        }
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit order (id: " + id + ")");
        return "redirect:/orders/1?sortField=id&sortDirection=asc";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/canceled/{id}")
    public String cancelOrderByAdmin(@PathVariable("id") Integer id, @RequestParam(required = false) String
            comment, Model model, Principal principal, RedirectAttributes ra) {
        try {
            boolean result = orderService.cancelOrderByAdmin(id, comment);
            if (!result) {
                ra.addFlashAttribute(MESSAGE, "The status of the order cannot be changed because the order has been cancelled.");
            } else {
                ra.addFlashAttribute(MESSAGE, "The status of the order was changed successfully.");
            }
        } catch (MessagingException | UnsupportedEncodingException e) {
            ra.addFlashAttribute(MESSAGE, e.getMessage());
        }
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit order (id: " + id + ")");
        return "redirect:/orders/1?sortField=id&sortDirection=asc";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/orders/canceledByUser/{id}")
    public String cancelOrderByUser(@PathVariable("id") Integer id, Model model, Principal principal, RedirectAttributes ra) {
        boolean result = orderService.cancelOrderByUser(id);
        if (!result) {
            ra.addFlashAttribute(MESSAGE, "The status of the order cannot be changed because the order has been cancelled.");
        } else {
            ra.addFlashAttribute(MESSAGE, "The status of the order was changed successfully.");
        }
        model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit order (id: " + id + ")");
        return "redirect:/orders/ordersForUser";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/orders/ordersForUser")
    public String ordersForUser(Model model, Principal principal, RedirectAttributes ra) {

        var user = userService.findByUsername(principal.getName());
        List<Order> orders = orderService.getOrderByUserId(user);
        List<Accident> accidents = accidentService.getAll();
        model.addAttribute(ORDERS_FOR_MODEL, orders);
        model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);

        return "order/ordersForUser";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/orders/searchOrdersByUser/{pageNumber}")
    public String searchOrderByUser(Model model,
                                    @RequestParam("name") String name,
                                    @PathVariable("pageNumber") int pageNumber,
                                    @RequestParam("sortField") String sortField,
                                    @RequestParam("sortDirection") String sortDirection) throws ServiceException {
        var page = orderService.findByUserNameOrSurname(pageNumber, sortField, sortDirection, name);
        List<Order> orders = orderService.findByUserNameOrSurname(name);
        List<Accident> accidents = accidentService.getAll();
        List<Payment> payments = paymentService.getAll();
        model.addAttribute(PAGE_FOR_MODEL, page);
        model.addAttribute(ORDERS_FOR_MODEL, orders);
        model.addAttribute(ACCIDENTS_FOR_MODEL, accidents);
        model.addAttribute(PAYMENTS_FOR_MODEL, payments);
        model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);
        String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
        model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
        model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);


        return "order/orders";
    }
}

