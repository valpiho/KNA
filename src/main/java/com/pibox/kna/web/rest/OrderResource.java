package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Order;
import com.pibox.kna.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderResource {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @GetMapping
    public List<Order> getAllOrders(){
        return orderServiceImpl.getAllOrders();
    }

    @PostMapping("/add")
    public Order addNewOrder(@RequestBody Order order){
        return orderServiceImpl.addNewOrder(order);
    }
}
