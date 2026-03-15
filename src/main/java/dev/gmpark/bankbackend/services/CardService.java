package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.CardEntity;
import dev.gmpark.bankbackend.mappers.CardMapper;
import dev.gmpark.bankbackend.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    // 매퍼 연결해서 쓰세요
    private final CardMapper cardMapper;

    public CommonResult createCard() {
        // 카드발급 로직 구현
        // userId 정보로 accountId select 해서 집어넣으면 될듯
        // cardMapper사용하세요
        return null;
    }
    public Pair<CommonResult, List<CardEntity>> getCardById(){
        // 카드조회 로직 구현
        return null;
    }
    public CommonResult updateCardStatus(){
        // 카드상태변경 로직 구현
        return null;
    }
    public CommonResult deleteCard(){
        // 카드삭제 로직 구현
        return null;
    }
}
