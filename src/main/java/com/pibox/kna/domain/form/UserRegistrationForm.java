package com.pibox.kna.domain.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationForm {

    private String username;
    private String firstName;
    private String lastName;
    private String privateEmail;
    @JsonProperty private boolean isClientOrDriver;
    private String plateNumber;
    private String companyEmail;
    private String phoneNumber;
    private String country;
    private String city;
    private String streetAddress;
    private String zipCode;
}
