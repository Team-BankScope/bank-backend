package dev.gmpark.bankbackend.entities;

import java.time.LocalDateTime;

public class UserPinEntity {
    private Long pidId;
    private Integer userId;
    private String pinHash;
    private Integer failCount;
    private LocalDateTime lockedUntil;
    private LocalDateTime updatedAt;
}

/*

--간편 비밀번호(PIN) 테이블
create table `bank`.`user_pin` (
pin_id         bigint auto_increment primary key,
user_id        int unsigned not null unique,
pin_hash       varchar(255) not null,
fail_count     int default 0 not null,
locked_until   datetime null, -- 일정 시간 동안 잠금 기능 추가
updated_at     datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
constraint fk_pin_user foreign key (user_id) references user (id) on delete cascade
);*/
