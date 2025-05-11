package ru.pionerpixel.banktransfer.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long toUserId;
    private BigDecimal amount;
}
