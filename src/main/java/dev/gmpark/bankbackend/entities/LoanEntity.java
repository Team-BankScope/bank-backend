package dev.gmpark.bankbackend.entities;


import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "loanId")
public class LoanEntity {
    private Integer loanId;
    private Integer userId;
    private Integer productId;
    private Long linked_account_id;
    private Long principalAmount;
    private Long outstandingAmount;
    private Double interestRate;
    private int paymentDay;
    private String status;
    private LocalDateTime overdueDate;
    private Long overdueAmount;
    private String maturityDate;
    private String createdAt;
    private String updatedAt;
}

