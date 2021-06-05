package com.pibox.kna.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pibox.kna.domain.Enumeration.Status;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "kna_order")
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
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Driver driver;
}
