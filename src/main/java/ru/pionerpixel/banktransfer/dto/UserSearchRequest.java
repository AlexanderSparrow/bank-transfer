package ru.pionerpixel.banktransfer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSearchRequest {
    private String name;
    private String email;
    private String phone;

    @Schema(description = "Дата рождения", example = "01.05.1993")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirthAfter;
    private int page = 0;
    private int size = 10;
}
