package ru.pionerpixel.banktransfer.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.pionerpixel.banktransfer.dto.LoginRequest;
import ru.pionerpixel.banktransfer.model.User;
import ru.pionerpixel.banktransfer.repository.UserRepository;
import ru.pionerpixel.banktransfer.security.JwtTokenUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public String login(LoginRequest request) throws BadRequestException {
        Optional<User> userOpt = Optional.empty();

        if (request.getEmail() != null) {
            userOpt = userRepository.findByEmails_Email(request.getEmail());
        } else if (request.getPassword() != null) {
            userOpt = userRepository.findByPhones_Phone(request.getPassword());
        }

        User user = userOpt.orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден")); //TODO
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Неверный пароль");
        }

        return jwtTokenUtil.generateToken(user.getId());
    }
}
