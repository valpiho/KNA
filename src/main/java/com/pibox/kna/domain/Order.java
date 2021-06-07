package com.pibox.kna.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pibox.kna.domain.Enumeration.OrderType;
import com.pibox.kna.domain.Enumeration.Status;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "kna_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String qrCode;
    private String title;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date shippedAt;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date receivedAt;
    private Status status;
    private OrderType orderType;
    private Boolean isActive;

    @ManyToOne
    private Client fromClient;

    @ManyToOne
    private Client toClient;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;

    public void setFromClient(Client client) {
        this.fromClient = client;
        client.addOrder(this);
    }

    public void setToClient(Client client) {
        this.fromClient = client;
        client.addOrder(this);
    }
}
