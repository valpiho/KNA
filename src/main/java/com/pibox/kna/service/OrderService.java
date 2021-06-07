package com.pibox.kna.service;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.Enumeration.Status;
import com.pibox.kna.domain.Order;
import com.pibox.kna.domain.User;
import com.pibox.kna.repository.ClientRepository;
import com.pibox.kna.repository.OrderRepository;
import com.pibox.kna.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order addNewOrder(Order order) {
        Order newOrder = new Order();
        String qrCode = generateQRCode();
        newOrder.setQrCode(qrCode);
        newOrder.setTitle(order.getTitle());
        newOrder.setDescription(order.getDescription());
        newOrder.setStatus(Status.OPENED);
        newOrder.setIsActive(true);
        newOrder.setCreatedAt(new Date());
        orderRepository.save(newOrder);
        return newOrder;
    }

    public Order getOrderById(Long id){
        return orderRepository.findById(id).orElse(null);
    }

    public Order getOrderByTitle(String title) {
        return orderRepository.findOrderByTitle(title);
    }

    public Order getOrderByQrCode(String qrCode){
        return orderRepository.findOrderByQrCode(qrCode);
    }

    public void deleteOrderByQrCode(String qrCode){
        Order order = orderRepository.findOrderByQrCode(qrCode);
        orderRepository.deleteById(order.getId());
    }

    //Method for drivers
    public List<Order> getOnlyOpenedOrders() {
        List<Order> allOrders = getAllOrders();
        List<Order> openedOrders = new ArrayList<>();
        int quantityOfOpenedOrders = 0;
        for (Order order : allOrders) {
            if (order.getStatus().equals(Status.OPENED)) {
                openedOrders.add(order);
                quantityOfOpenedOrders++;
            }
        }
        System.out.println(quantityOfOpenedOrders);
        return openedOrders;
    }

    public List<Order> getOnlyInProgressOrders() {
        List<Order> allOrders = getAllOrders();
        List<Order> inProgressOrders = new ArrayList<>();
        int quantityOfInProgressOrders = 0;
        for (Order order : allOrders) {
            if (order.getStatus().equals(Status.IN_PROGRESS)) {
                inProgressOrders.add(order);
                quantityOfInProgressOrders++;
            }
        }
        System.out.println(quantityOfInProgressOrders);
        return inProgressOrders;
    }

    public List<Order> getOnlyClosedOrders() {
        List<Order> allOrders = getAllOrders();
        List<Order> closedOrders = new ArrayList<>();
        int quantityOfClosedOrders = 0;
        for (Order order : allOrders) {
            if (order.getStatus().equals(Status.CLOSED)) {
                closedOrders.add(order);
                quantityOfClosedOrders++;
            }
        }
        System.out.println(quantityOfClosedOrders);
        return closedOrders;
    }

    public List<Order> getOrdersByUsername(String username) {
        List<Order> allOrders = getAllOrders();
        List<Order> orderListByUsername = new ArrayList<>();
        User user = userRepository.findUserByUsername(username);
        return orderListByUsername;
    }

    //for method addNewOrder
    private String generateQRCode() {
        return RandomStringUtils.randomAlphanumeric(15);
    }
}
