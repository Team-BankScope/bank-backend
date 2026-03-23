package dev.gmpark.bankbackend.services;

import dev.gmpark.bankbackend.entities.AccountEntity;
import dev.gmpark.bankbackend.entities.TransactionHistoryEntity;
import dev.gmpark.bankbackend.mappers.AccountMapper;
import dev.gmpark.bankbackend.mappers.TransactionHistoryMapper;
import dev.gmpark.bankbackend.results.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
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
    public Pair<TransactionResult,TransactionHistoryEntity> deposit(String accountNumber, Long amount, String description, Long taskId) {
        if (amount == null || amount <= 0) {
            return Pair.of(TransactionResult.FAILURE, null);
        }

        AccountEntity account = accountMapper.selectAccountByAccountNumber(accountNumber);
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            return Pair.of(TransactionResult.FAILURE_INVALID_ACCOUNT, null);
        }

        Long newBalance = account.getBalance() + amount;
        
        int updated = accountMapper.updateBalance(account.getAccountId(), newBalance);
        if (updated == 0) {
            return Pair.of(TransactionResult.FAILURE, null);
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
            return Pair.of(TransactionResult.FAILURE, null);
        }

        return Pair.of(TransactionResult.SUCCESS, transaction);
    }

    @Transactional
    public Pair<TransactionResult, TransactionHistoryEntity> withdraw(String accountNumber, String accountPassword, Long amount, String description, Long taskId) {
        if (amount == null || amount <= 0) {
            return Pair.of(TransactionResult.FAILURE, null);
        }

        AccountEntity account = accountMapper.selectAccountByAccountNumber(accountNumber);
        if (account == null || !"ACTIVE".equals(account.getStatus())) {
            return Pair.of(TransactionResult.FAILURE_INVALID_ACCOUNT, null);
        }

        if (!BCrypt.checkpw(accountPassword, account.getAccountPassword())) {
            return Pair.of(TransactionResult.FAILURE_INVALID_PASSWORD, null);
        }

        if (account.getBalance() < amount) {
            return Pair.of(TransactionResult.FAILURE_INSUFFICIENT_BALANCE, null);
        }

        Long newBalance = account.getBalance() - amount;

        int updated = accountMapper.updateBalance(account.getAccountId(), newBalance);
        if (updated == 0) {
            return Pair.of(TransactionResult.FAILURE, null);
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
            return Pair.of(TransactionResult.FAILURE, null);
        }

        return Pair.of(TransactionResult.SUCCESS, transaction);
    }

    @Transactional
    public Pair<TransactionResult, TransactionHistoryEntity> transfer(String fromAccountNumber, String accountPassword, String toAccountNumber, Long amount, String description, Long taskId) {
        if (amount == null || amount <= 0) {
            return Pair.of(TransactionResult.FAILURE, null);
        }

        if (fromAccountNumber == null || fromAccountNumber.equals(toAccountNumber)) {
            return Pair.of(TransactionResult.FAILURE_INVALID_ACCOUNT, null);
        }

        AccountEntity fromAccount = accountMapper.selectAccountByAccountNumber(fromAccountNumber);
        if (fromAccount == null || !"ACTIVE".equals(fromAccount.getStatus())) {
            return Pair.of(TransactionResult.FAILURE_INVALID_ACCOUNT, null);
        }

        if (!BCrypt.checkpw(accountPassword, fromAccount.getAccountPassword())) {
            return Pair.of(TransactionResult.FAILURE_INVALID_PASSWORD, null);
        }

        if (fromAccount.getBalance() < amount) {
            return Pair.of(TransactionResult.FAILURE_INSUFFICIENT_BALANCE, null);
        }

        AccountEntity toAccount = accountMapper.selectAccountByAccountNumber(toAccountNumber);
        if (toAccount == null || !"ACTIVE".equals(toAccount.getStatus())) {
            return Pair.of(TransactionResult.FAILURE_INVALID_TO_ACCOUNT, null);
        }

        Long newFromBalance = fromAccount.getBalance() - amount;
        Long newToBalance = toAccount.getBalance() + amount;

        int updatedFrom = accountMapper.updateBalance(fromAccount.getAccountId(), newFromBalance);
        int updatedTo = accountMapper.updateBalance(toAccount.getAccountId(), newToBalance);

        if (updatedFrom == 0 || updatedTo == 0) {
            return Pair.of(TransactionResult.FAILURE, null);
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
            return Pair.of(TransactionResult.FAILURE, null);
        }

        // 이체의 경우, 출금(송금)한 쪽의 거래 내역을 주로 반환합니다.
        return Pair.of(TransactionResult.SUCCESS, fromTransaction);
    }
}
