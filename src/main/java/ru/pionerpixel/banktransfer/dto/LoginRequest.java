package ru.pionerpixel.banktransfer.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email; // или phone, на уровне сервиса будет проверка
    private String phone;
    private String password;
}
