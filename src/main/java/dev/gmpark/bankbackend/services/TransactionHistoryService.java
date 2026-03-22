package dev.gmpark.bankbackend.services;

import dev.gmpark.bankbackend.entities.AccountEntity;
import dev.gmpark.bankbackend.entities.TransactionHistoryEntity;
import dev.gmpark.bankbackend.mappers.AccountMapper;
import dev.gmpark.bankbackend.mappers.TransactionHistoryMapper;
import dev.gmpark.bankbackend.results.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {
    private final TransactionHistoryMapper transactionHistoryMapper;
    private final AccountMapper accountMapper;

    @Transactional
    public TransactionResult deposit(String accountNumber, Long amount, String description, Long taskId) {
        if (amount == null || amount <= 0) {
            return TransactionResult.FAILURE;
        }

        AccountEntity account = accountMapper.selectAccountByAccountNumber(accountNumber);
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            return TransactionResult.FAILURE_INVALID_ACCOUNT;
        }

        Long newBalance = account.getBalance() + amount;
        
        int updated = accountMapper.updateBalance(account.getAccountId(), newBalance);
        if (updated == 0) {
            return TransactionResult.FAILURE;
        }

        TransactionHistoryEntity transaction = TransactionHistoryEntity.builder()
                .accountId(account.getAccountId())
                .userId(account.getUserId())
                .taskId(taskId)
                .transactionType("DEPOSIT")
                .amount(amount)
                .balanceAfter(newBalance)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        int inserted = transactionHistoryMapper.insertTransaction(transaction);
        if (inserted == 0) {
            return TransactionResult.FAILURE;
        }

        return TransactionResult.SUCCESS;
    }

    @Transactional
    public TransactionResult withdraw(String accountNumber, String accountPassword, Long amount, String description, Long taskId) {
        if (amount == null || amount <= 0) {
            return TransactionResult.FAILURE;
        }

        AccountEntity account = accountMapper.selectAccountByAccountNumber(accountNumber);
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            return TransactionResult.FAILURE_INVALID_ACCOUNT;
        }

        if (!BCrypt.checkpw(accountPassword, account.getAccountPassword())) {
            return TransactionResult.FAILURE_INVALID_PASSWORD;
        }

        if (account.getBalance() < amount) {
            return TransactionResult.FAILURE_INSUFFICIENT_BALANCE;
        }

        Long newBalance = account.getBalance() - amount;

        int updated = accountMapper.updateBalance(account.getAccountId(), newBalance);
        if (updated == 0) {
            return TransactionResult.FAILURE;
        }

        TransactionHistoryEntity transaction = TransactionHistoryEntity.builder()
                .accountId(account.getAccountId())
                .userId(account.getUserId())
                .taskId(taskId)
                .transactionType("WITHDRAW")
                .amount(amount)
                .balanceAfter(newBalance)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        int inserted = transactionHistoryMapper.insertTransaction(transaction);
        if (inserted == 0) {
            return TransactionResult.FAILURE;
        }

        return TransactionResult.SUCCESS;
    }

    @Transactional
    public TransactionResult transfer(String fromAccountNumber, String accountPassword, String toAccountNumber, Long amount, String description, Long taskId) {
        if (amount == null || amount <= 0) {
            return TransactionResult.FAILURE;
        }

        if (fromAccountNumber == null || fromAccountNumber.equals(toAccountNumber)) {
            return TransactionResult.FAILURE_INVALID_ACCOUNT;
        }

        AccountEntity fromAccount = accountMapper.selectAccountByAccountNumber(fromAccountNumber);
        if (fromAccount == null || !"ACTIVE".equals(fromAccount.getStatus())) {
            return TransactionResult.FAILURE_INVALID_ACCOUNT;
        }

        if (!BCrypt.checkpw(accountPassword, fromAccount.getAccountPassword())) {
            return TransactionResult.FAILURE_INVALID_PASSWORD;
        }

        if (fromAccount.getBalance() < amount) {
            return TransactionResult.FAILURE_INSUFFICIENT_BALANCE;
        }

        AccountEntity toAccount = accountMapper.selectAccountByAccountNumber(toAccountNumber);
        if (toAccount == null || !"ACTIVE".equals(toAccount.getStatus())) {
            return TransactionResult.FAILURE_INVALID_TO_ACCOUNT;
        }

        Long newFromBalance = fromAccount.getBalance() - amount;
        Long newToBalance = toAccount.getBalance() + amount;

        int updatedFrom = accountMapper.updateBalance(fromAccount.getAccountId(), newFromBalance);
        int updatedTo = accountMapper.updateBalance(toAccount.getAccountId(), newToBalance);

        if (updatedFrom == 0 || updatedTo == 0) {
            return TransactionResult.FAILURE;
        }

        TransactionHistoryEntity fromTransaction = TransactionHistoryEntity.builder()
                .accountId(fromAccount.getAccountId())
                .userId(fromAccount.getUserId())
                .taskId(taskId)
                .transactionType("TRANSFER_OUT")
                .amount(amount)
                .balanceAfter(newFromBalance)
                .description(toAccount.getAccountAlias() + "에게 이체: " + description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        TransactionHistoryEntity toTransaction = TransactionHistoryEntity.builder()
                .accountId(toAccount.getAccountId())
                .userId(toAccount.getUserId())
                .taskId(taskId)
                .transactionType("TRANSFER_IN")
                .amount(amount)
                .balanceAfter(newToBalance)
                .description(fromAccount.getAccountAlias() + "로부터 입금: " + description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        int insertedFrom = transactionHistoryMapper.insertTransaction(fromTransaction);
        int insertedTo = transactionHistoryMapper.insertTransaction(toTransaction);

        if (insertedFrom == 0 || insertedTo == 0) {
            return TransactionResult.FAILURE;
        }

        return TransactionResult.SUCCESS;
    }
}
