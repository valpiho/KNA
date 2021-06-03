package com.pibox.kna.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 20, message = "Must be between 2 and 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 20, message = "Must be between 2 and 20 characters")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 20, message = "Must be between 2 and 20 characters")
    private String username;

    private String password;

    @Email
    @NotBlank
    private String email;

    @JsonProperty
    private boolean isClientOrDriver;
    private String driverPlateNumber;
    private String clientEmail;
    private String clientPhoneNumber;
    private String clientCountry;
    private String clientCity;
    private String clientStreetAddress;
    private String clientZipCode;
    private boolean isActive;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date joinDate;

    private String role;
    private String[] authorities;


    @Builder
    public UserDTO(String firstName, String lastName, String username, String email, boolean isClientOrDriver, String driverPlateNumber,
                   String clientEmail, String clientPhoneNumber, String clientCountry, String clientCity, String clientStreetAddress, String clientZipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.isClientOrDriver = isClientOrDriver;
        this.driverPlateNumber = driverPlateNumber;
        this.clientEmail = clientEmail;
        this.clientPhoneNumber = clientPhoneNumber;
        this.clientCountry = clientCountry;
        this.clientCity = clientCity;
        this.clientStreetAddress = clientStreetAddress;
        this.clientZipCode = clientZipCode;
    }
}
