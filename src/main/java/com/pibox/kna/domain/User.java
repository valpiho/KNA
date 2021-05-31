package com.pibox.kna.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@Table(name = "kna_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String imageUrl;
    private boolean isActive;
    private Date joinDate;
    private String role;
    private String[] authorities;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_users",
            joinColumns = @JoinColumn(name = "contact_user_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> contacts = new ArrayList<>();
}
