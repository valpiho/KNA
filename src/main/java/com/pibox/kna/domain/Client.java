package com.pibox.kna.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "kna_client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String phoneNumber;
    private String country;
    private String city;
    private String streetAddress;
    private String zipCode;


    @OneToOne(mappedBy = "client")
    private User user;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<Order> orders;
}
