package com.pibox.kna.repository;

import com.pibox.kna.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOrderByQrCode(String qrCode);

    @Query("SELECT u FROM Order u WHERE u.createdBy.user.username = ?1")
    List<Order> findAllByUsername(String username);

    @Query("SELECT u FROM Order u WHERE u.status = 0")
    List<Order> getAllOpenOrders();
}
