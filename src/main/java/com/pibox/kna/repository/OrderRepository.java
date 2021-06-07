package com.pibox.kna.repository;

import com.pibox.kna.domain.Enumeration.Status;
import com.pibox.kna.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOrderByTitle(String title);

    //List<Order> findOrderByUserName(String username);

    Order findOrderByQrCode(String qrCode);
}
