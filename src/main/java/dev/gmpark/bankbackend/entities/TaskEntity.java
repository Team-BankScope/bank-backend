package dev.gmpark.bankbackend.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskEntity {
    private Long taskId;
    private String userEmail;
    private String ticketNumber;
    private String taskType;
    private String taskDetailType;
    private String assignedLevel;
    private Integer expectedWaitingTime;
    private String status;
    private Integer memberId;            // 💡 int -> Integer (WAITING 상태일 때 null 허용)
    private Integer ranking;             // 💡 int -> Integer (순번이 없을 때 null 허용)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}