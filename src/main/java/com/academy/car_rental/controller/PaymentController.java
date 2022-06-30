package com.academy.car_rental.controller;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;
import com.academy.car_rental.model.entity.type.OrderStatus;
import com.academy.car_rental.service.AccidentService;
import com.academy.car_rental.service.OrderService;
import com.academy.car_rental.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static com.academy.car_rental.constant.Constants.*;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final AccidentService accidentService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/payments")
    public String showPaymentsList(Model model) {

        List<Payment> payments = paymentService.getAll();
        model.addAttribute("payments", payments);

        return "payment/payments";
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/payments/card/new")
    public String showCardForm(Integer orderId,
                               Integer carId,
                               Model model,
                               Principal principal,
                               RedirectAttributes ra) {
        try {
            var order = orderService.getById(orderId);
            var payment = paymentService.createNewPayment(order);
            model.addAttribute(ORDER_FOR_MODEL, order);
            model.addAttribute(PAYMENT_FOR_MODEL, payment);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new payment");

        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The payment wasn't add." + e.getMessage());

        }
        return "payment/card";
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/payments/new")
    public String showNewForm(Integer orderId,
                              String number,
                              String validity1,
                              String validity2,
                              Model model,
                              Principal principal,
                              RedirectAttributes ra) {
        try {
            var order = orderService.getById(orderId);
            var payment = paymentService.createNewPayment(order);
            model.addAttribute(ORDER_FOR_MODEL, order);
            model.addAttribute(PAYMENT_FOR_MODEL, payment);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new payment");
            model.addAttribute(CARD_DATA_FOR_MODEL, "Order was paid card №" + number + ". Validity:" + validity1 + ".Passport №" + validity2);

        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The payment wasn't add." + e.getMessage());

        }
        return "payment/created";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/payments/save")
    public String savePayment(Payment payment, RedirectAttributes ra) {
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);
        orderService.save(order);
        paymentService.save(payment);
        ra.addFlashAttribute(MESSAGE, "The payment has been saved successfully.");
        return "redirect:payment/payments";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/payments/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Payment payment = paymentService.getById(id);
            model.addAttribute(PAYMENT_FOR_MODEL, payment);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Edit payment (id: " + id + ")");
            return "payment/payment";
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The payment wasn't found." + e.getMessage());
            return "redirect:payment/payments";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/payments/delete/{id}")
    public String deletePayments(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            paymentService.delete(id);
            model.addAttribute(MESSAGE, "Payment was deleted successfully");
        } catch (ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The payment wasn't deleted." + e.getMessage());
            return "redirect:payment/payments";
        }
        return "redirect:payment/payments";
    }


    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/payments/accident/new")
    public String payForAccident(Integer orderId, BigDecimal totalCost, Principal principal, Model model, RedirectAttributes ra) {

        try {
            var order = orderService.getById(orderId);
            var payment = paymentService.createNewPaymentForAccident(order, totalCost);

            model.addAttribute(ORDER_FOR_MODEL, order);
            model.addAttribute(PAYMENT_FOR_MODEL, payment);
            model.addAttribute(PAGE_TITLE_FOR_MODEL, "Add new payment");


        } catch (
                ServiceException e) {
            ra.addFlashAttribute(MESSAGE, "The payment wasn't add." + e.getMessage());

        }
        return "payment/created";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/payments/showPaymentsByOrder/{id}")
    public String showAccidentByOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            var payment = paymentService.getById(id);
            var order = orderService.getById(payment.getOrder().getId());
            List<Payment> payments = paymentService.getAllPaymentsByOrder(order);
            BigDecimal totalCost = BigDecimal.valueOf(0);
            for (Payment entry : payments) {
                totalCost = totalCost.add(entry.getTotalPrice());
            }
            model.addAttribute(TOTAL_COST_FOR_MODEL, totalCost);
            model.addAttribute(PAYMENTS_FOR_MODEL, payments);
            model.addAttribute(ORDER_FOR_MODEL, order);

        } catch (ServiceException e) {
            model.addAttribute(MESSAGE, "There are no payments for this order");
            return "redirect:/orders";
        }

        return "payment/payment";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/payments/searchPaymentsByDate")
    public String searchPaymentByDate(Model model,
                                      @RequestParam("date") String date) {

        List<Payment> payments = paymentService.findByStartDate(date);

        model.addAttribute(PAYMENTS_FOR_MODEL, payments);

        return "payment/payments";
    }



}
