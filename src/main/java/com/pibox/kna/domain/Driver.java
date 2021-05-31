package com.pibox.kna.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "kna_driver")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;

    @OneToOne(mappedBy = "driver")
    private User user;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private Set<Order> orders;

    public Driver() { }

    public Driver(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
