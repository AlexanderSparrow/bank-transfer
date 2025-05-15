package ru.pionerpixel.banktransfer.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.repository.AccountRepository;
import ru.pionerpixel.banktransfer.service.BalanceUpdateService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceAccrualScheduler {

    private final AccountRepository accountRepository;
    private final BalanceUpdateService balanceUpdateService;

    @Scheduled(fixedRate = 30_000)
    public void accrueInterest() {
        int page = 0;
        int size = 100;
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accountPage;

        int updated = 0;

        do {
            accountPage = accountRepository.findAll(pageable);
            for (Account account : accountPage.getContent()) {
                try {
                    balanceUpdateService.update(account.getId());
                    updated++;
                } catch (Exception e) {
                    log.warn("Ошибка при обновлении аккаунта {}: {}", account.getId(), e.getMessage());
                }
            }
            pageable = pageable.next();
        } while (accountPage.hasNext());

        log.info("Обновлено {} аккаунтов", updated);
    }
}
