package dev.gmpark.bankbackend.entities;

import dev.gmpark.bankbackend.enums.OtpStatus;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "otpId")
public class DigitalOtpEntity {

    private Long otpId;
    private Integer userId;

    private String pinHash;        // Bcrypt로 암호화된 핀번호
    private Integer pinFailCount;  // 핀번호 실패 횟수

    private String otpSecretKey;   // AES로 암호화된 OTP 시드키
    private Integer otpFailCount;  // OTP 실패 횟수

    private OtpStatus status;      // Enum 사용으로 안전성 확보

    private LocalDateTime issuedAt;
    private LocalDateTime lastUsedAt;
}