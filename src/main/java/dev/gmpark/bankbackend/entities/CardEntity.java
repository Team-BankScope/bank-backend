package dev.gmpark.bankbackend.entities;


import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "cardId")
public class CardEntity {
    private Long cardId;
    private Integer userId;
    private Long accountId;
    private String cardCompany;
    private String cardName;
    private String cardNumber;
    private String cardType;
    private String cvc;
    private String status;
    private String validThru;
    private LocalDateTime issuedAt;
}

/*
-- 부가 서비스 도메인 (카드, 대출)
CREATE TABLE `bank`.`card`
        (
card_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
user_id        INT UNSIGNED                         NOT NULL,
account_id     BIGINT                               NULL,     -- account 테이블과 JOIN 할 핵심 연결고리!
        -- UI 노출 및 최소 결제 정보
card_name      VARCHAR(50)                          NULL,     -- 카드 상품명
card_number    VARCHAR(20)                          NOT NULL UNIQUE,
card_type      VARCHAR(20)                          NOT NULL, -- 'CHECK' (체크), 'CREDIT' (신용)
cvc            VARCHAR(3)                           NOT NULL,
    -- 상태 및 유효기간
status         VARCHAR(20) DEFAULT 'ACTIVE'         NOT NULL,
valid_thru     VARCHAR(5)                           NOT NULL, -- 'MM/YY' 형식
issued_at      DATETIME    DEFAULT CURRENT_TIMESTAMP,

    -- 외래키(Foreign Key) 제약 조건
CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES user (id),
CONSTRAINT fk_card_account FOREIGN KEY (account_id) REFERENCES account (account_id) ON UPDATE CASCADE
);*/
