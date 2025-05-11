package ru.pionerpixel.banktransfer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailDto {
    @NotNull
    @NotBlank(message = "Email не указан")
    @Pattern(regexp = "^(?!\\s*$).+", message = "Email не может состоять только из пробелов")
    private String email;
}
