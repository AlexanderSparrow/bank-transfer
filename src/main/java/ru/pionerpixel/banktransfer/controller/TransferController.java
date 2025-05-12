package ru.pionerpixel.banktransfer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pionerpixel.banktransfer.dto.TransferRequest;
import ru.pionerpixel.banktransfer.service.TransferService;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {
    private final TransferService transferService;

    @Operation(
            summary = "Перевод средств",
            description = "Позволяет перевести деньги от текущего пользователя к другому по ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств или неверный запрос"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "404", description = "Получатель не найден")
    })
    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        transferService.transfer(transferRequest);
        return ResponseEntity.ok().build();
    }
}
