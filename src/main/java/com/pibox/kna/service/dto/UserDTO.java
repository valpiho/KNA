package com.pibox.kna.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private String driverPlateNumber;
    private String clientEmail;
    private String clientPhoneNumber;
    private String clientCountry;
    private String clientCity;
    private String clientStreetAddress;
    private String clientZipCode;
    private boolean isActive;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date joinDate;
    private String role;
    private String[] authorities;
}
