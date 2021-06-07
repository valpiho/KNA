package com.pibox.kna.service.dto;
import lombok.Data;

@Data
public class OrderDTO {

    private String title;
    private String description;
    private Boolean isInbound;
    private String username;
}
