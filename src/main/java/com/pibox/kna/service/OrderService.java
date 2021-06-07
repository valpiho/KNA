package com.pibox.kna.service;

import com.pibox.kna.domain.Enumeration.Role;
import com.pibox.kna.domain.Enumeration.Status;
import com.pibox.kna.domain.Order;
import com.pibox.kna.domain.User;
import com.pibox.kna.exceptions.domain.NotFoundException;
import com.pibox.kna.repository.OrderRepository;
import com.pibox.kna.repository.UserRepository;
import com.pibox.kna.service.dto.OrderDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.pibox.kna.constants.OrderConstant.NO_ORDER_FOUND_BY_QRCODE;
import static com.pibox.kna.constants.SecurityConstant.ACCESS_DENIED_MESSAGE;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
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
        newOrder.setCreatedBy(authUser.getClient());
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

    public Order getOrderByQrCode(String qrCode) throws NotFoundException {
        Order order = orderRepository.findOrderByQrCode(qrCode);
        if (order == null) {
            throw new NotFoundException(NO_ORDER_FOUND_BY_QRCODE + qrCode);
        }
        return order;
    }

    public List<Order> getClientOrders(String authUsername) {
        return orderRepository.findAllByUsername(authUsername);
    }

    public void deleteOrderByQrCode(String authUsername, String qrCode) throws NotFoundException {
        Order order = orderRepository.findOrderByQrCode(qrCode);
        User user = userRepository.findUserByUsername(authUsername);
        if (order == null) {
            throw new NotFoundException(NO_ORDER_FOUND_BY_QRCODE + qrCode);
        }
        if (!order.getCreatedBy().getUser().equals(user) || !user.getRole().equals(Role.ROLE_ADMIN.name())) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
        orderRepository.deleteById(order.getId());
    }

    //Method for drivers
//    public List<Order> getOnlyOpenedOrders() {
//        List<Order> allOrders = getAllOrders();
//        List<Order> openedOrders = new ArrayList<>();
//        int quantityOfOpenedOrders = 0;
//        for (Order order : allOrders) {
//            if (order.getStatus().equals(Status.OPENED)) {
//                openedOrders.add(order);
//                quantityOfOpenedOrders++;
//            }
//        }
//        System.out.println(quantityOfOpenedOrders);
//        return openedOrders;
//    }

//    public List<Order> getOnlyInProgressOrders() {
//        List<Order> allOrders = getAllOrders();
//        List<Order> inProgressOrders = new ArrayList<>();
//        int quantityOfInProgressOrders = 0;
//        for (Order order : allOrders) {
//            if (order.getStatus().equals(Status.IN_PROGRESS)) {
//                inProgressOrders.add(order);
//                quantityOfInProgressOrders++;
//            }
//        }
//        System.out.println(quantityOfInProgressOrders);
//        return inProgressOrders;
//    }


//    public List<Order> getOnlyClosedOrders() {
//        List<Order> allOrders = getAllOrders();
//        List<Order> closedOrders = new ArrayList<>();
//        int quantityOfClosedOrders = 0;
//        for (Order order : allOrders) {
//            if (order.getStatus().equals(Status.CLOSED)) {
//                closedOrders.add(order);
//                quantityOfClosedOrders++;
//            }
//        }
//        System.out.println(quantityOfClosedOrders);
//        return closedOrders;
//    }

//    public List<Order> getOrdersByUsername(String username) {
//        List<Order> allOrders = getAllOrders();
//        List<Order> orderListByUsername = new ArrayList<>();
//        User user = userRepository.findUserByUsername(username);
//        return orderListByUsername;
//    }

    //for method addNewOrder
    private String generateQRCode() {
        return RandomStringUtils.randomAlphanumeric(15);
    }
}
