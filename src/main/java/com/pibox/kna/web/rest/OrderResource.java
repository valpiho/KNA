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

    @GetMapping("/id/{id}")
    public Order getOrderById(@PathVariable("id") Long id){
        return orderService.getOrderById(id);
    }

    @GetMapping("/title/{title}")
    public Order getOrderByTitle(@PathVariable("title") String title){
        return orderService.getOrderByTitle(title);
    }

    @GetMapping("/{qrCode}")
    public Order getOrderByQrCode(@PathVariable("qrCode") String qrCode){
        return orderService.getOrderByQrCode(qrCode);
    }

    @DeleteMapping("/delete/{qrCode}")
    public void deleteOrderByQrCode(@PathVariable("qrCode") String qrCode){
        orderService.deleteOrderByQrCode(qrCode);
    }

    //Method for Drivers. Drivers can see ONLY OPENED Orders.
    @GetMapping("/openedOrders")
    public List<Order> getOnlyOpenedOrders(){
        return orderService.getOnlyOpenedOrders();
    }

    @GetMapping("/inProgressOrders")
    public List<Order> getOnlyInProgressOrders(){
        return orderService.getOnlyInProgressOrders();
    }

    @GetMapping("/closedOrders")
    public List<Order> getOnlyClosedOrders(){
        return orderService.getOnlyClosedOrders();
    }

    @GetMapping("/username/{username}")
    public List<Order> getOrdersByUsername(@PathVariable("username") String username){
        return orderService.getOrdersByUsername(username);
    }
}
