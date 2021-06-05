package com.pibox.kna.service;

import com.pibox.kna.domain.Enumeration.Status;
import com.pibox.kna.domain.Order;
import com.pibox.kna.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public Order addNewOrder(Order order){
        Order newOrder = new Order();
        newOrder.setTitle(order.getTitle());
        newOrder.setDescription(order.getDescription());
        newOrder.setStatus(Status.OPENED);
        newOrder.setIsActive(true);
        newOrder.setCreatedAt(new Date());
        orderRepository.save(newOrder);
        return newOrder;
    }

    public Order getOrderByTitle(String title){
        return orderRepository.findOrderByTitle(title);
    }
}
