package com.pibox.kna.domain;

import com.pibox.kna.domain.Enumeration.Status;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "kna_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String qrCode;
    private String title;
    private String description;
    private Date createdAt;
    private Date shippedAt;
    private Date receivedAt;
    private Status status;
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;
}