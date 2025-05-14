package ru.pionerpixel.banktransfer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pionerpixel.banktransfer.dto.EmailDto;
import ru.pionerpixel.banktransfer.dto.PhoneDto;
import ru.pionerpixel.banktransfer.dto.UserDto;
import ru.pionerpixel.banktransfer.dto.UserSearchRequest;
import ru.pionerpixel.banktransfer.service.UserService;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Текущий пользователь", description = "Возвращает профиль текущего аутентифицированного пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя возвращены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.ok(userService.getMe(authUser()));
    }

    @Operation(summary = "Добавление пользователем нового email")
    @PostMapping("/email")
    public ResponseEntity<Void> addEmail(@Valid @RequestBody EmailDto dto) {
        userService.addEmail(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление пользователем своего email")
    @DeleteMapping("/email")
    public ResponseEntity<Void> deleteEmail(@Valid @RequestBody EmailDto dto) {
        userService.deleteEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавление пользователем нового номера телефона")
    @PostMapping("/phone")
    public ResponseEntity<Void> addPhone(@RequestBody PhoneDto dto) {
        userService.addPhone(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удаление пользователем своего номера телефона")
    @DeleteMapping("/phone")
    public ResponseEntity<Void> deletePhone(@RequestBody PhoneDto dto) {
        userService.deletePhone(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск пользователей с параметрами и пагинацией")
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> search(@Valid @ModelAttribute UserSearchRequest request) {
        return ResponseEntity.ok(userService.search(request));
    }

    private Long authUser() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken auth
                ? (Long) auth.getPrincipal()
                : null;
    }
}
