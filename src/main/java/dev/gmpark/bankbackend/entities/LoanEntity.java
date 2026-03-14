package dev.gmpark.bankbackend.entities;

public class LoanEntity {
}

/*
create table `bank`.`loan`
        (
loan_id            bigint auto_increment primary key,
user_id            int unsigned                         not null,
product_id         int unsigned                         not null, -- financial_product 참조
principal_amount   bigint                               not null, -- 대출 원금
outstanding_amount bigint                               not null, -- 남은 상환액
interest_rate      decimal(5, 2)                        not null, -- 적용 이율
status             varchar(20) default 'ACTIVE'         not null, -- 'ACTIVE', 'PAID_OFF', 'OVERDUE'
maturity_date      date                                 not null, -- 만기일
created_at         datetime    default CURRENT_TIMESTAMP,
constraint fk_loan_user foreign key (user_id) references user (id)
        );*/
