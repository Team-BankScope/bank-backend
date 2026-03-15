package dev.gmpark.bankbackend.entities;


import lombok.*;

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
    private Long principalAmount;
    private Long outstandingAmount;
    private Double interestRate;
    private String status;
    private String maturityDate;
    private String createdAt;
    private String updatedAt;
}

