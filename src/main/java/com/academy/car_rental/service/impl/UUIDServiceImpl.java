package com.academy.car_rental.service.impl;

import com.academy.car_rental.service.UUIDService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UUIDServiceImpl implements UUIDService {
    public String getRandomString() {
        return UUID.randomUUID().toString();
    }
}
