package ru.pionerpixel.banktransfer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.pionerpixel.banktransfer.exception.AppException;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.repository.AccountRepository;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceUpdateService {

    private final AccountRepository accountRepository;

    @Transactional
    public void update(Long accountId) {
        Account account = accountRepository.findByUser_Id(accountId)
                .orElseThrow(() -> new AppException("Аккаунт не найден: " + accountId, HttpStatus.NOT_FOUND));

        BigDecimal initial = account.getInitialBalance();
        BigDecimal max = initial.multiply(BigDecimal.valueOf(2.07));
        BigDecimal current = account.getCurrentBalance();
        BigDecimal next = current.multiply(BigDecimal.valueOf(1.1));

        if (next.compareTo(max) <= 0) {
            account.setCurrentBalance(next);
            log.info("Баланс {} увеличен до {}", account.getUser().getName(), next);
        } else if (current.compareTo(max) < 0) {
            account.setCurrentBalance(max);
            log.info("Баланс {} достиг лимита {}", account.getUser().getName(), max);
        } else {
            log.info("Баланс {} уже на максимуме", account.getUser().getName());
        }
    }
}
