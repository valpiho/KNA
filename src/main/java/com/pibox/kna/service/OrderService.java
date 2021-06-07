package com.pibox.kna.service;

import com.pibox.kna.domain.Client;
import com.pibox.kna.domain.Enumeration.Status;
import com.pibox.kna.domain.Order;
import com.pibox.kna.domain.User;
import com.pibox.kna.repository.ClientRepository;
import com.pibox.kna.repository.OrderRepository;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.OrderDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order addNewOrder(String authUsername, OrderDTO orderDto) {
        User authUser = userRepository.findUserByUsername(authUsername);
        User user = userRepository.findUserByUsername(orderDto.getUsername());
        Order newOrder = new Order();
        String qrCode = generateQRCode();
        newOrder.setQrCode(qrCode);
        newOrder.setTitle(orderDto.getTitle());
        newOrder.setDescription(orderDto.getDescription());
        newOrder.setStatus(Status.OPENED);
        newOrder.setIsActive(true);
        newOrder.setCreatedAt(new Date());
        if (orderDto.getIsInbound()) {
            newOrder.setFromClient(user.getClient());
            newOrder.setToClient(authUser.getClient());
        } else {
            newOrder.setFromClient(authUser.getClient());
            newOrder.setToClient(user.getClient());
        }
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
