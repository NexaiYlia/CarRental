package com.academy.car_rental.service.impl;

import com.academy.car_rental.model.entity.Car;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.entity.Payment;
import com.academy.car_rental.model.entity.User;
import com.academy.car_rental.model.entity.type.*;
import com.academy.car_rental.model.repository.AccidentRepository;
import com.academy.car_rental.model.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentServiceImplTest {
    @Autowired
    private PaymentServiceImpl paymentService;
    @MockBean
    private PaymentRepository paymentRepository;
    @MockBean
    private AccidentRepository accidentRepository;

    private Payment payment1;
    private Payment payment2;
    private User user1;

    private Car car1;
    private Order order1;
    private List<Payment> paymentList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        car1 = Car.builder()
                .id(1)
                .gearbox(GearboxType.AUTOMATIC)
                .brand("VW")
                .model("passat")
                .carType(CarType.ECONOMY)
                .engineType(EngineType.DIESEL)
                .manufacturedYear("2013")
                .pricePerDay(BigDecimal.valueOf(50))
                .build();
        user1 = User.builder()
                .id(1)
                .username("asdfg")
                .password("asdfgh")
                .name("asdfg")
                .surname("asdfg")
                .phoneNumber("375293579520")
                .email("asdf@gmail.com")
                .verificationStatus(false)
                .verificationCode("cvbnmzx")
                .build();
        order1 = Order.builder()
                .id(1)
                .cost(BigDecimal.valueOf(120))
                .status(OrderStatus.NEW)
                .endDate(LocalDate.parse("2022-05-04"))
                .startDate(LocalDate.parse("2022-05-03"))
                .car(car1)
                .user(user1)
                .build();

        payment1 = Payment.builder()
                .id(1)
                .status(PaymentStatus.APPROVED)
                .paymentDate(LocalDate.parse("2022-05-03"))
                .totalPrice(BigDecimal.valueOf(200))
                .order(order1)
                .build();
        payment2 = Payment.builder()
                .id(2)
                .status(PaymentStatus.APPROVED)
                .paymentDate(LocalDate.parse("2022-05-03"))
                .totalPrice(BigDecimal.valueOf(200))
                .order(order1)
                .build();

        paymentList.add(payment1);
        paymentList.add(payment2);
    }

    @Test
    void createNewPayment() {
        var payment1 = Payment.builder()
                .status(PaymentStatus.APPROVED)
                .order(order1)
                .paymentDate(LocalDate.now())
                .totalPrice(order1.getCost())
                .build();
        var payment = paymentService.createNewPayment(order1);
        assertEquals(payment, payment1);

    }

    @Test
    void createNewPaymentForAccidentTest() {
        var payment1 = Payment.builder()
                .status(PaymentStatus.APPROVED)
                .order(order1)
                .paymentDate(LocalDate.now())
                .totalPrice(BigDecimal.valueOf(200))
                .build();
        var payment = paymentService.createNewPaymentForAccident(order1, BigDecimal.valueOf(200));
        assertEquals(payment, payment1);

    }

    @Test
    void findByStartDateTest() {
        when(paymentService.findByStartDate("2022-05-03")).thenReturn(paymentList);
        var maybepayments = paymentService.findByStartDate("2022-05-03");
        assertTrue(maybepayments.size() == 2);

    }
}



