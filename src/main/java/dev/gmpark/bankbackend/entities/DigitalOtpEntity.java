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
    private String optSecreteKey;
    private OtpStatus status;      // Enum 사용으로 안전성 확보

    private LocalDateTime issuedAt;
    private LocalDateTime lastUsedAt;
}


/*
-- 디지털 OTP 설정 테이블
create table digital_otp (
        otp_id         bigint auto_increment primary key,
        user_id        int unsigned not null unique,
        otp_secret_key varchar(255) not null, -- KMS 등으로 암호화 권장
fail_count     int default 0 not null,
status         enum('ACTIVE', 'LOCKED', 'REVOKED') default 'ACTIVE' not null,
issued_at      datetime default CURRENT_TIMESTAMP,
last_used_at   datetime null,
constraint fk_otp_user foreign key (user_id) references user (id) on delete cascade
);
*/
