package com.pibox.kna.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Size(min = 8, max = 20, message = "Must be between 8 and 20 characters")
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
}
