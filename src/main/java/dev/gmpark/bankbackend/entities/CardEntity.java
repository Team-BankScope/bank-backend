package dev.gmpark.bankbackend.entities;

public class CardEntity {
}

/*
create table `bank`.`card`
        (
card_id        bigint auto_increment primary key,
user_id        int unsigned                         not null,
account_id     bigint                               null, -- 결제 연결 계좌 (신용카드는 없을 수도 있음)
card_number    varchar(20)                          not null unique,
card_type      varchar(20)                          not null, -- 'CHECK', 'CREDIT'
status         varchar(20) default 'ACTIVE'         not null, -- 'ACTIVE', 'SUSPENDED', 'EXPIRED'
issued_at      datetime    default CURRENT_TIMESTAMP,
valid_thru     varchar(5)                           not null, -- 'MM/YY' 형식
constraint fk_card_user foreign key (user_id) references user (id)
        );*/
