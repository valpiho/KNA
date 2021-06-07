package com.pibox.kna.repository;

import com.pibox.kna.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findClientById(Long id);

    Client findClientByUser_Username(String username);
}
