package ru.pionerpixel.banktransfer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pionerpixel.banktransfer.dto.TransferRequest;
import ru.pionerpixel.banktransfer.exception.AppException;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.repository.AccountRepository;
import ru.pionerpixel.banktransfer.utils.AuthUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private static final BigDecimal MIN_TRANSFER_AMOUNT = new BigDecimal("0.01");
    private static final BigDecimal MAX_TRANSFER_AMOUNT = new BigDecimal("1000000.00");
    private final AccountRepository accountRepository;
    private final AuthUtils authUtils;

    @Transactional
    public void transfer(TransferRequest request) {
        Long fromUserId = authUtils.getCurrentUserId();
        Long toUserId = request.getToUserId();
        BigDecimal amount = request.getAmount();

        validateTransfer(fromUserId, toUserId, amount);

        Account fromAccount = accountRepository.findByUser_Id(fromUserId)
                .orElseThrow(() -> new AppException("Аккаунт отправителя не найден", HttpStatus.NOT_FOUND));
        Account toAccount = accountRepository.findByUser_Id(toUserId)
                .orElseThrow(() -> new AppException("Аккаунт получателя не найден", HttpStatus.NOT_FOUND));

        // Принято проверять еще активность аккаунта, но такого поля нет в ТЗ

        if (fromAccount.getCurrentBalance().compareTo(amount) < 0) {
            throw new AppException("Недостаточно средств. Текущий баланс: " + fromAccount.getCurrentBalance() + ", запрошено: " + amount,
                    HttpStatus.BAD_REQUEST);
        }

        fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(amount));
        toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(amount));
        log.info("Перевод выполнен: {} переведено от пользователя {} к пользователю {}. Новый баланс отправителя: {}",
                amount, fromUserId, toUserId, fromAccount.getCurrentBalance());
    }

    private void validateTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new AppException("Нельзя перевести средства самому себе", HttpStatus.FORBIDDEN);
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException("Сумма перевода должна быть больше нуля", HttpStatus.BAD_REQUEST);
        }

        if (amount.compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            throw new AppException("Сумма перевода не может быть меньше " + MIN_TRANSFER_AMOUNT, HttpStatus.BAD_REQUEST);
        }

        if (amount.compareTo(MAX_TRANSFER_AMOUNT) > 0) {
            throw new AppException("Сумма перевода не может превышать " + MAX_TRANSFER_AMOUNT, HttpStatus.BAD_REQUEST);
        }
    }
}
