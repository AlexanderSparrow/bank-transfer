package ru.pionerpixel.banktransfer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pionerpixel.banktransfer.dto.LoginRequest;
import ru.pionerpixel.banktransfer.exception.AppException;
import ru.pionerpixel.banktransfer.model.User;
import ru.pionerpixel.banktransfer.repository.UserRepository;
import ru.pionerpixel.banktransfer.security.JwtTokenUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Авторизация в сервисе по связке телефон-пароль или email-пароль
     *
     * @param request - объект {@link LoginRequest} телефон (или email) и пароль
     * @return - токен
     */

    public String login(LoginRequest request) {
        Optional<User> userOpt = Optional.empty();

        if (request.getEmail() != null) {
            userOpt = userRepository.findByEmails_Email(request.getEmail());
        } else if (request.getPassword() != null) {
            userOpt = userRepository.findByPhones_Phone(request.getPhone());
        }

        User user = userOpt.orElseThrow(() -> new AppException("Пользователь не найден.", HttpStatus.NOT_FOUND));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException("Неверный пароль", HttpStatus.BAD_REQUEST);
        }

        return jwtTokenUtil.generateToken(user.getId());
    }
}
