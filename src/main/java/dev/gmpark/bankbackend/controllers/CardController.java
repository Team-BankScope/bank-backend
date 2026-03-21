package dev.gmpark.bankbackend.controllers;


import dev.gmpark.bankbackend.entities.CardEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.services.CardService;
import dev.gmpark.bankbackend.results.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "카드(Card)", description = "카드 발급 및 관리 API")
@RestController
@RequestMapping(value = "/api/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @Operation(summary = "카드 발급", description = "새로운 카드(체크/신용)를 발급받습니다.")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> createCard( CardEntity card, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }

        // TODO: CardService를 호출하여 카드 발급 로직 처리 (카드번호, CVC, 유효기간 자동 생성 등)
        CommonResult result = this.cardService.createCard(card, user);
        response.put("result", result.name());

        // response.put("result", "SUCCESS");
        return response;
    }

    @Operation(summary = "내 카드 목록 조회", description = "로그인한 사용자가 보유한 모든 카드 목록을 조회합니다.")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getMyCards(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }

        // TODO: CardService를 호출하여 해당 유저의 카드 목록 조회
        List<CardEntity> cards = this.cardService.getCardsByUserId(user.getId());
        response.put("result", CommonResult.SUCCESS.name());
        response.put("cards", cards);

        // response.put("result", "SUCCESS"); // 임시 응답 (실제 로직으로 대체됨)
        return response;
    }

    @Operation(summary = "카드 상세 조회", description = "특정 카드의 상세 정보(CVC, 유효기간 등)를 조회합니다.")
    @GetMapping(value = "/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCardDetail(@PathVariable("cardId") Long cardId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }

        // TODO: CardService를 호출하여 특정 카드 조회 (본인 소유 확인 필요)
        Pair<CommonResult, CardEntity> result = this.cardService.getCardById(cardId, user.getId());
        response.put("result", result.getLeft().name());

        if (result.getLeft() == CommonResult.SUCCESS) {
            response.put("card", result.getRight());
        }

        // response.put("result", "SUCCESS"); // 임시 응답 (실제 로직으로 대체됨)
        return response;
    }

    @Operation(summary = "카드 상태 변경", description = "카드의 상태를 변경합니다 (예: 분실 신고, 정지 해제).")
    @PatchMapping(value = "/{cardId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateCardStatus(
            @PathVariable("cardId") Long cardId,
            @RequestParam("status") String status,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }

        // TODO: CardService를 호출하여 상태 변경 (본인 소유 확인 필요)
        CommonResult result = this.cardService.updateCardStatus(cardId, status, user.getId());
        response.put("result", result.name());

        // response.put("result", "SUCCESS"); // 임시 응답 (실제 로직으로 대체됨)
        return response;
    }

    @Operation(summary = "카드 해지/삭제", description = "카드를 해지(또는 삭제)합니다.")
    @DeleteMapping(value = "/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteCard(@PathVariable("cardId") Long cardId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");

        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }

        // TODO: CardService를 호출하여 카드 삭제/해지 (본인 소유 확인 필요)
        CommonResult result = this.cardService.deleteCard(cardId, user.getId());
        response.put("result", result.name());

        // response.put("result", "SUCCESS"); // 임시 응답 (실제 로직으로 대체됨)
        return response;
    }
}
