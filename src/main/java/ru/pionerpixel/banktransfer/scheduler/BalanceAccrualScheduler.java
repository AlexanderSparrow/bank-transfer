package ru.pionerpixel.banktransfer.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceAccrualScheduler {

    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void accrueInterest() {
        List<Account> accounts = accountRepository.findAll();
        List<Account> updatedAccounts = new ArrayList<>();

        for (Account account : accounts) {
            BigDecimal initial = account.getInitialBalance();
            BigDecimal maxBalance = initial.multiply(BigDecimal.valueOf(2.07));
            BigDecimal current = account.getCurrentBalance();
            BigDecimal next = current.multiply(BigDecimal.valueOf(1.1));

            if (next.compareTo(maxBalance) <= 0) {
                account.setCurrentBalance(next);
                updatedAccounts.add(account);
                log.info("Баланс пользователя {} увеличился на 10%. Новый баланс: {}", account.getUser().getName(), next);
            } else if (current.compareTo(maxBalance) < 0) {
                account.setCurrentBalance(maxBalance);
                updatedAccounts.add(account);
                log.info("Баланс пользователя {} достиг максимума. Новый баланс: {}", account.getUser().getName(), maxBalance);
            }
        }

        if (!updatedAccounts.isEmpty()) {
            accountRepository.saveAll(updatedAccounts);
            log.info("Сохранено {} аккаунтов с обновленным балансом", updatedAccounts.size());
        }
    }
}