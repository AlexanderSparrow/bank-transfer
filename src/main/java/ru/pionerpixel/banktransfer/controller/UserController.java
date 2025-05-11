package ru.pionerpixel.banktransfer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.pionerpixel.banktransfer.dto.EmailDto;
import ru.pionerpixel.banktransfer.dto.PhoneDto;
import ru.pionerpixel.banktransfer.dto.UserDto;
import ru.pionerpixel.banktransfer.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get current user info", description = "Returns full profile of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.ok(userService.getMe(authUser()));
    }

    @PostMapping("/email")
    public ResponseEntity<Void> addEmail (@Valid @RequestBody EmailDto dto) {
        userService.addEmail(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/email")
    public ResponseEntity<Void> deleteEmail (@Valid @RequestBody EmailDto dto) throws ChangeSetPersister.NotFoundException {//TODO
        userService.deleteEmail(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phone")
    public ResponseEntity<Void> addPhone(@RequestBody PhoneDto dto) {
        userService.addPhone(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/phone")
    public ResponseEntity<Void> deletePhone(@RequestBody PhoneDto dto) {
        userService.deletePhone(dto);
        return ResponseEntity.noContent().build();
    }

    private Long authUser() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken auth
                ? (Long) auth.getPrincipal()
                : null;
    }
}
