package com.pibox.kna.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "kna_drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;

    @OneToOne(mappedBy = "driver")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Driver() { }

    public Driver(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
