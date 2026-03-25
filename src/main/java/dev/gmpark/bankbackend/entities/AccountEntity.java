package dev.gmpark.bankbackend.entities;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "accountId")
public class AccountEntity {
    private Long accountId;
    private Integer userId;
    private String accountNumber;
    private String accountType;
    private Long balance; // 현재 잔액( 보유금액)
    private BigDecimal interestRate;
    private BigDecimal maturityDate;
    private String accountPassword; // 통장 비밀번호
    private String status;         // status(ACTIVE, DORMANT, CLOSED 등)
    private Integer passwordFailCount; // 비밀번호 실패 횟수
    private String accountAlias; //계좌 별칭
    private LocalDateTime createdAt; // 계좌 개설일
    private LocalDateTime lastTransactionAt; // 최근거래일시
}