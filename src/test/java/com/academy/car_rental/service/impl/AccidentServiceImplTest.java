package com.academy.car_rental.service.impl;

import com.academy.car_rental.model.repository.AccidentRepository;
import com.academy.car_rental.model.repository.OrderRepository;
import com.academy.car_rental.service.AccidentService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccidentServiceImplTest {
    @Autowired
    private AccidentService accidentService;
    @MockBean
    private AccidentRepository accidentRepository;
    @MockBean
    private OrderRepository orderRepository;

}
