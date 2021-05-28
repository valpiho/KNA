package com.pibox.kna.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "kna_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
}
