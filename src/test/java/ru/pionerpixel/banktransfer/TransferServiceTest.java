package ru.pionerpixel.banktransfer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pionerpixel.banktransfer.dto.TransferRequest;
import ru.pionerpixel.banktransfer.exception.AppException;
import ru.pionerpixel.banktransfer.model.Account;
import ru.pionerpixel.banktransfer.repository.AccountRepository;
import ru.pionerpixel.banktransfer.service.TransferService;
import ru.pionerpixel.banktransfer.utils.AuthUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    AccountRepository accountRepository;
    @Mock
    AuthUtils authUtils;

    @InjectMocks
    TransferService transferService;

    private final Long senderId = 1L;
    private final Long recipientId = 2L;
    private final BigDecimal startingBalance = new BigDecimal("1000.00");

    @Test
    void shouldTransferMoneySuccessfully() {
        TransferRequest request = new TransferRequest(recipientId, new BigDecimal("100.00"));

        Account sender = new Account();
        sender.setId(senderId);
        sender.setCurrentBalance(startingBalance);

        Account recipient = new Account();
        recipient.setId(recipientId);
        recipient.setCurrentBalance(BigDecimal.ZERO);

        when(authUtils.getCurrentUserId()).thenReturn(senderId);
        when(accountRepository.findByUser_Id(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByUser_Id(recipientId)).thenReturn(Optional.of(recipient));

        transferService.transfer(request);

        assertEquals(new BigDecimal("900.00"), sender.getCurrentBalance());
        assertEquals(new BigDecimal("100.00"), recipient.getCurrentBalance());
    }

    @Test
    void shouldThrowIfInsufficientFunds() {
        TransferRequest request = new TransferRequest(recipientId, new BigDecimal("5000.00"));

        Account sender = new Account();
        sender.setId(senderId);
        sender.setCurrentBalance(new BigDecimal("100.00"));

        when(authUtils.getCurrentUserId()).thenReturn(senderId);
        when(accountRepository.findByUser_Id(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByUser_Id(recipientId)).thenReturn(Optional.of(new Account()));

        AppException ex = assertThrows(AppException.class, () -> transferService.transfer(request));
        assertTrue(ex.getMessage().contains("Недостаточно средств"));
    }

    @Test
    void shouldThrowWhenTransferringToSelf() {
        TransferRequest request = new TransferRequest(senderId, new BigDecimal("100.00"));
        when(authUtils.getCurrentUserId()).thenReturn(senderId);

        AppException ex = assertThrows(AppException.class, () -> transferService.transfer(request));
        assertEquals("Нельзя перевести средства самому себе", ex.getMessage());
    }

    @Test
    void shouldThrowIfRecipientNotFound() {
        TransferRequest request = new TransferRequest(recipientId, new BigDecimal("50.00"));
        Account sender = new Account();
        sender.setId(senderId);
        sender.setCurrentBalance(new BigDecimal("500.00"));

        when(authUtils.getCurrentUserId()).thenReturn(senderId);
        when(accountRepository.findByUser_Id(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByUser_Id(recipientId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> transferService.transfer(request));
        assertTrue(ex.getMessage().contains("Аккаунт получателя не найден"));
    }
}
