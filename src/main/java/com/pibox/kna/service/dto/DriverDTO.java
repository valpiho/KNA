package com.pibox.kna.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class DriverDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String userEmail;
    private String imageUrl;
    private String driverPlateNumber;
    private boolean isActive;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date joinDate;
    private String role;
    private String[] authorities;
}
