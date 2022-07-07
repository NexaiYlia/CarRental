package com.academy.car_rental.service.impl;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.*;
import com.academy.car_rental.model.entity.type.*;
import com.academy.car_rental.model.repository.AccidentRepository;
import com.academy.car_rental.model.repository.CarRepository;
import com.academy.car_rental.model.repository.OrderRepository;
import com.academy.car_rental.model.repository.UserRepository;
import com.academy.car_rental.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.academy.car_rental.constant.Constants.ASC;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {
    @Autowired
    private OrderServiceImpl orderService;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CarRepository carRepository;
    @MockBean
    private AccidentRepository accidentRepository;
    @MockBean
    private EmailService emailService;

    private Order order1;
    private Order order2;
    private Car car1;
    private Car car2;
    private User user1;
    private User user2;
    private List<Order> orderList = new ArrayList<>();

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

        car1 = Car.builder()
                .id(2)
                .gearbox(GearboxType.MANUAL)
                .brand("SKODA")
                .model("Rapid")
                .carType(CarType.STANDART)
                .engineType(EngineType.PETROL)
                .manufacturedYear("2020")
                .pricePerDay(BigDecimal.valueOf(60))
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

        user2 = User.builder()
                .id(2)
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
                .user(user1)
                .car(car1)
                .status(OrderStatus.APPROVED)
                .startDate(LocalDate.parse("2022-05-04"))
                .endDate(LocalDate.parse("2022-05-05"))
                .cost(BigDecimal.valueOf(50))
                .build();

        order2 = Order.builder()
                .id(2)
                .user(user2)
                .car(car2)
                .status(OrderStatus.NEW)
                .startDate(LocalDate.parse("2022-05-04"))
                .endDate(LocalDate.parse("2022-05-05"))
                .cost(BigDecimal.valueOf(60))
                .build();

    }

    @Test
    void createNewOrderTest() {

        when(orderService.existsByDateOfOrder(car1.getId(), LocalDate.parse("2022-05-04"), LocalDate.parse("2022-05-05"))).thenReturn(true);
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());
        when(userRepository.findByUsername(user1.getName())).thenReturn(user1);
        when(carRepository.getById(car1.getId())).thenReturn(car1);
        when(orderService.countCost(car1, " 2022-05-04", "2022-05-05")).thenReturn(BigDecimal.valueOf(50));
        var orderTest = orderService.createNewOrder(car1.getId(), "2022-05-04", "2022-05-05", (Principal) user1);
        assertEquals(orderTest, order1);

    }

    @Test
    void countCostTest() {


    }

    @Test
    void existsByDateOfOrderTest() {


    }

    @Test
    void findByStartDateTest() {


    }

    @Test
    void findByStatusTest() {


    }

    @Test
    void cancelOrderByAdminTest() {


    }

    @Test
    void cancelOrderByUserTest() {
//        when(orderRepository.findById(order1.getId())).thenReturn(Optional.of(order1));
//        verify(order1, times(1)).getStatus();
//        orderService.cancelOrderByUser(order1.getId());
//        assertEquals(order1.getStatus(), OrderStatus.CANCELED_BY_USER);
//        assertNull(order1.getStartDate());
//        assertNull(order1.getEndDate());
//        verify(orderRepository, times(1)).findById(1);
//        verify(orderRepository, times(1)).saveAndFlush(order1);

    }

    @Test
    void getOrderByUserIdTest() {


    }
}
