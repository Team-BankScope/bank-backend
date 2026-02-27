package dev.gmpark.bankbackend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequestDto {
    private String taskType;
    private String taskDetailType;
}