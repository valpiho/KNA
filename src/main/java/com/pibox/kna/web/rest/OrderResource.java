package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Order;
import com.pibox.kna.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderResource {

    @Autowired
    private OrderService orderService;

    @GetMapping("/all")
    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }

    @PostMapping("/add")
    public Order addNewOrder(@RequestBody Order order){
        return orderService.addNewOrder(order);
    }

    @GetMapping("/{title}")
    public Order getOrderByTitle(@PathVariable("title") String title){
        return orderService.getOrderByTitle(title);
    }
}
