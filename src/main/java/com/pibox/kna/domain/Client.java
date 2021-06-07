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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "kna_clients_orders",
            joinColumns = @JoinColumn(name = "order_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"))
    private List<Order> orders = new ArrayList<>();

    public Client() { }

    public Client(String email, String phoneNumber, String country, String city, String streetAddress, String zipCode) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.city = city;
        this.streetAddress = streetAddress;
        this.zipCode = zipCode;
    }
}
