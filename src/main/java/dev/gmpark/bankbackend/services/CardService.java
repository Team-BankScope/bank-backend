package dev.gmpark.bankbackend.services;

import org.apache.commons.lang3.tuple.Pair;
import dev.gmpark.bankbackend.entities.AccountEntity;
import dev.gmpark.bankbackend.entities.CardEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.mappers.AccountMapper;
import dev.gmpark.bankbackend.mappers.CardMapper;
import dev.gmpark.bankbackend.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    // 매퍼 연결해서 쓰세요
    private final CardMapper cardMapper;
    private final AccountMapper accountMapper; // 계좌 조회를 위해 추가

    public CommonResult createCard(CardEntity card, UserEntity user) {
        // 필수 입력값 검증
        // card.getAccountId()는 이제 필요 없음. user.getId()로 계좌를 찾을 것이기 때문.
        if (user == null || card.getCardType() == null) {
            return CommonResult.FAILURE;
        }

        // 1. 유저 ID로 계좌 정보 조회하여 accountId 세팅
        AccountEntity account = accountMapper.selectAccountById(user.getId());
        if (account == null) {
            return CommonResult.FAILURE; // 연결할 계좌가 없는 경우
        }

        card.setUserId(user.getId());
        card.setCardCompany("우리은행");
        card.setAccountId(account.getAccountId()); // 조회한 accountId 세팅

        // 2. 카드 번호, CVC, 유효기간 자동 생성
        card.setCardNumber(generateCardNumber());
        card.setCvc(generateCvc());
        card.setValidThru(generateValidThru());
        card.setStatus("ACTIVE");
        card.setIssuedAt(LocalDateTime.now());

        // 3. 매퍼를 통해 DB insert
        int result = this.cardMapper.insertCard(card);
        return result > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public List<CardEntity> getCardsByUserId(Integer userId) {
        return this.cardMapper.selectCardsByUserId(userId);
    }

    public Pair<CommonResult, CardEntity> getCardById(Long cardId, Integer userId){
        // 카드조회 로직 구현
        CardEntity card = this.cardMapper.selectCardByIdAndUserId(cardId, userId);
        if (card != null) {
            return Pair.of(CommonResult.SUCCESS, card);
        }
        return Pair.of(CommonResult.FAILURE, null);
    }

    public CommonResult updateCardStatus(Long cardId, String status, Integer userId){
        // 카드상태변경 로직 구현
        if (status == null || status.trim().isEmpty()) {
            return CommonResult.FAILURE;
        }
        int result = this.cardMapper.updateCardStatus(cardId, status, userId);
        return result > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommonResult deleteCard(Long cardId, Integer userId){
        // 카드삭제 로직 구현
        int result = this.cardMapper.deleteCard(cardId, userId);
        return result > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    // --- 난수 생성 헬퍼 메서드 (자동 생성 로직) ---
    private String generateCardNumber() {
        Random random = new Random();
        return String.format("%04d-%04d-%04d-%04d",
                random.nextInt(10000), random.nextInt(10000), random.nextInt(10000), random.nextInt(10000));
    }

    private String generateCvc() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private String generateValidThru() {
        // 발급일 기준 5년 후 유효기간 (MM/yy 형식)
        LocalDateTime expiryDate = LocalDateTime.now().plusYears(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }
}