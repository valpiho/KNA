package com.pibox.kna.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMiniDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private boolean driver;
    private boolean client;
}
