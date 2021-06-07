package com.pibox.kna.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "kna_clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String phoneNumber;
    private String country;
    private String city;
    private String streetAddress;
    private String zipCode;

    @OneToOne(mappedBy = "client")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> inboundOrders = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> outboundOrders = new ArrayList<>();

    public Client() { }

    public Client(String email, String phoneNumber, String country, String city, String streetAddress, String zipCode) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.streetAddress = streetAddress;
        this.zipCode = zipCode;
    }

    public void addInboundOrder(Order order) {
        this.inboundOrders.add(order);
    }

    public void addOutboundOrder(Order order) {
        this.outboundOrders.add(order);
    }
}
