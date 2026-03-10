package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.AccountEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.mappers.AccountMapper;
import dev.gmpark.bankbackend.mappers.UserMapper;
import dev.gmpark.bankbackend.results.AccountResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Pair<AccountResult, AccountEntity> createAccount(Integer userId, String accountType, String accountAlias, String accountPassword) {
        
        // 사용자 존재 여부 확인
        UserEntity user = userMapper.selectUserById(userId);
        if (user == null) {
            return Pair.of(AccountResult.FAILURE_USER_NOT_EXIST, null);
        }
        // 사용자 유저여부 확인
        if ( user.getUserType() != null && !user.getUserType().equals("customer")) {
            return Pair.of(AccountResult.FAILURE, null);
        }

        AccountEntity account  = new AccountEntity();
        account.setUserId(userId);
        // 계좌번호 생성 및 중복 검사
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (accountMapper.countByAccountNumber(accountNumber) > 0);
        
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setAccountAlias(accountAlias);
        account.setCreatedAt(LocalDateTime.now());
        account.setLastTransactionAt(LocalDateTime.now());
        account.setBalance(0L);
        account.setStatus("ACTIVE");
        account.setAccountPassword(encoder.encode(accountPassword));
        account.setPasswordFailCount(0);
        int insert = this.accountMapper.insertAccount(account);
        if ( insert > 0 ) {
            return Pair.of(AccountResult.SUCCESS,account);
        }
        return Pair.of(AccountResult.FAILURE, null);

    }
    public AccountResult checkAccountPassword( Integer id, String accountPassword) {
        AccountEntity account = this.accountMapper.selectAccountById(id);
        if (account == null) {
            return AccountResult.FAILURE;
        }
        if (BCrypt.checkpw(accountPassword, account.getAccountPassword())) {
            return AccountResult.SUCCESS;
        } else {
            return AccountResult.FAILURE;
        }
    }

    private String generateAccountNumber() {
        Random random = new Random();
        // 예: 110-XXX-XXXXXX (신한은행 스타일)
        int part1 = 110;
        int part2 = random.nextInt(900) + 100; // 100 ~ 999
        int part3 = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.format("%d-%d-%d", part1, part2, part3);
    }
}
