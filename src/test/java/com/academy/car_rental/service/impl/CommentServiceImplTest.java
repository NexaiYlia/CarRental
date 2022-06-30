package com.academy.car_rental.service.impl;

import com.academy.car_rental.model.repository.CommentRepository;
import com.academy.car_rental.model.repository.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentServiceImplTest {
    @Autowired
    private CommentServiceImpl commentService;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private UserRepository userRepository;
}
