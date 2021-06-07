package com.pibox.kna.web.rest;

import com.pibox.kna.domain.Order;
import com.pibox.kna.security.jwt.JWTTokenProvider;
import com.pibox.kna.service.OrderService;
import com.pibox.kna.service.dto.OrderDTO;
import com.pibox.kna.service.dto.OrderResDTO;
import com.pibox.kna.service.dto.OrderResMiniDTO;
import com.pibox.kna.service.utility.MapperService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderResource {

    private final OrderService orderService;
    private final JWTTokenProvider jwtTokenProvider;
    private final MapperService mapper;

    public OrderResource(OrderService orderService,
                         JWTTokenProvider jwtTokenProvider,
                         MapperService mapper) {
        this.orderService = orderService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<OrderResMiniDTO>> getOrders(@RequestHeader("Authorization") String token){
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        List<Order> orders = orderService.getClientOrders(authUsername);
        return new ResponseEntity<>(mapper.toListOfOrderResMiniDTO(orders), OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('client:create')")
    public ResponseEntity<OrderResDTO> addNewOrder(@RequestBody OrderDTO orderDto,
                                                   @RequestHeader("Authorization") String token) {
        String authUsername = jwtTokenProvider.getUsernameFromDecodedToken(token);
        Order order = orderService.addNewOrder(authUsername, orderDto);
        return new ResponseEntity<>(mapper.toOrderResDto(order), CREATED);
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

//    //Method for Drivers. Drivers can see ONLY OPENED Orders.
//    @GetMapping("/openedOrders")
//    public List<Order> getOnlyOpenedOrders(){
//        return orderService.getOnlyOpenedOrders();
//    }
//
//    @GetMapping("/inProgressOrders")
//    public List<Order> getOnlyInProgressOrders(){
//        return orderService.getOnlyInProgressOrders();
//    }
//
//    @GetMapping("/closedOrders")
//    public List<Order> getOnlyClosedOrders(){
//        return orderService.getOnlyClosedOrders();
//    }
//
//    @GetMapping("/username/{username}")
//    public List<Order> getOrdersByUsername(@PathVariable("username") String username){
//        return orderService.getOrdersByUsername(username);
//    }
}
