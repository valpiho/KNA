package com.pibox.kna.service.impl;

import com.pibox.kna.domain.Enumeration.Status;
import com.pibox.kna.domain.Order;
import com.pibox.kna.repository.OrderRepository;
import com.pibox.kna.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public Order addNewOrder(Order order){
        return orderRepository.save(order);
    }
}
