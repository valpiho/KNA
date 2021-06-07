package com.pibox.kna.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OrderResMiniDTO {

    private String qrCode;
    private String title;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date createdAt;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date shippedAt;
    @JsonFormat(pattern = "dd/MM/yyyy") private Date receivedAt;
    private String orderType;
    private String status;
    private boolean isActive;
}
