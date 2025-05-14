package ru.pionerpixel.banktransfer.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferRequest {
    @NotNull
    private Long toUserId;

    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("1000000.00")
    private BigDecimal amount;
}

