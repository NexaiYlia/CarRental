package com.academy.car_rental.service.impl;

import com.academy.car_rental.exception.ServiceException;
import com.academy.car_rental.model.entity.Accident;
import com.academy.car_rental.model.entity.Order;
import com.academy.car_rental.model.repository.AccidentRepository;
import com.academy.car_rental.model.repository.OrderRepository;
import com.academy.car_rental.service.AccidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccidentServiceImpl implements AccidentService {
    private final AccidentRepository accidentRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Accident> getAll() {
        return accidentRepository.findAll();
    }

    @Override
    public Accident getById(Integer id) throws ServiceException {
        Optional<Accident> result = accidentRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ServiceException("Accident not found");
    }

    @Override
    public void save(Accident accident) {
        accidentRepository.save(accident);
    }

    @Override
    public void delete(Integer id) {
        accidentRepository.deleteById(id);
    }

    public List<Accident> getAllAccidentsByOrder(Order order) {
        List<Accident> accidents = accidentRepository.findAllAccidentsByOrder(order);
        return accidents;
    }

    @Override
    public void changeAccidentPaymentStatus(Order order) {
        List<Accident> accidents = accidentRepository.findAllAccidentsByOrder(order);
        for (Accident entry : accidents) {
            entry.setPaid(true);
            accidentRepository.saveAndFlush(entry);
        }
    }

    public boolean createNewAccident(Accident accident, BigDecimal cost, String description,  Integer orderId) {
        var order = orderRepository.findById(orderId).get();
        Accident accidentNew = Accident.builder()
                .cost(cost)
                .description(description)
                .date(order.getStartDate())
                .order(order)
                .isPaid(false)
                .build();
        save(accidentNew);
        return true;
    }
}
