package com.academy.car_rental.service.impl;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;
import com.academy.car_rental.model.entity.type.PaymentStatus;
import com.academy.car_rental.model.repository.AccidentRepository;
import com.academy.car_rental.model.repository.PaymentRepository;
import com.academy.car_rental.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccidentRepository accidentRepository;

    @Override
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getById(Integer id) throws ServiceException {
        Optional<Payment> result = paymentRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ServiceException("Payment not found");
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public void delete(Integer id) throws ServiceException {
        Long count = paymentRepository.countById(id);
        if (count == null || count == 0) {
            throw new ServiceException("Could not find any payment with id: " + id);

        }
        paymentRepository.deleteById(id);
    }

    @Override
    public Payment createNewPayment(Order order) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.APPROVED)
                .order(order)
                .paymentDate(LocalDate.now())
                .totalPrice(order.getCost())
                .build();
        save(payment);
        return payment;
    }

    @Override
    public Payment createNewPaymentForAccident(Order order, BigDecimal totalCost) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.APPROVED)
                .order(order)
                .paymentDate(LocalDate.now())
                .totalPrice(totalCost)
                .build();
        save(payment);
        return payment;
    }

    @Override
    public List<Payment> getAllPaymentsByOrder(Order order) {
        List<Payment> payments = paymentRepository.findAllPaymentByOrder(order);
        return payments;
    }

    @Override
    public List<Payment> findByStartDate(String date) {
        LocalDate startDate = LocalDate.parse(date);
        List<Payment> payments = paymentRepository.findByPaymentDate(startDate);
        return payments;
    }


}

