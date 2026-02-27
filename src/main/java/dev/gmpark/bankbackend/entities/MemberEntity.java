package dev.gmpark.bankbackend.entities;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MemberEntity {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Integer level;
    private String auth;
    private String team;
    private Integer status;
    private int counterNumber;
    private LocalDate joinDate;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}