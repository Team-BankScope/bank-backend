package dev.gmpark.bankbackend.entities;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "subscriptionId")
public class ProductSubscriptionEntity {
    private Integer subscriptionId;
    private Integer userId;
    private Integer productId;
    private Long taskId;
    private Long amount;
    private Integer durationMonths;
    private String status;
    private LocalDateTime createdAt;
}
