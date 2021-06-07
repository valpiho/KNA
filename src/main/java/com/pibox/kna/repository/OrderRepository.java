package com.pibox.kna.repository;

import com.pibox.kna.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findOrderByTitle(String title);

    Order findOrderByQrCode(String qrCode);

    @Query("SELECT u.orders FROM Client u WHERE u.user.username = ?1")
    List<Order> findAllByUsername(String username);


}
