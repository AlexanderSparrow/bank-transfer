package ru.pionerpixel.banktransfer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.pionerpixel.banktransfer.dto.TransferRequest;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.repository.AccountRepository;
import ru.pionerpixel.banktransfer.utils.AuthUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransferService {
    private final AccountRepository accountRepository;
    private final AuthUtils authUtils;

    public void transfer(TransferRequest request) {
        Long fromUserId = authUtils.getCurrentUserId();

        Account fromAccount = accountRepository.findById(fromUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Аккаунт отправителя не найден"));

        Account toAccount = accountRepository.findByUser_Id(request.getToUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Аккаунт получателя не найден"));

        BigDecimal amount = request.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK>"); //TODO
        }

        if(fromAccount.getCurrentBalance().compareTo(amount) < 0){
            throw new IllegalArgumentException("Недостаточно средств для перевода запрошенной суммы. Баланс счета: " + fromAccount.getCurrentBalance());
        }

        fromAccount.setCurrentBalance(fromAccount.getCurrentBalance().subtract(amount));
        accountRepository.save(fromAccount);
        toAccount.setCurrentBalance(toAccount.getCurrentBalance().add(amount));
        accountRepository.save(toAccount);
        log.info("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK>: " + fromAccount.getCurrentBalance());//TODO
    }
}
