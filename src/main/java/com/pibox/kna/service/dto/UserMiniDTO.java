package com.pibox.kna.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserMiniDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private boolean isActive;
    private boolean isContact;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date joinDate;
    private String role;
    private String[] authorities;
}
