package dev.gmpark.bankbackend.controllers;

import dev.gmpark.bankbackend.entities.FinancialProductEntity;
import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.results.FinancialProductResult;
import dev.gmpark.bankbackend.services.FinancialProductService;
import dev.gmpark.bankbackend.vos.BoardPageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "금융 상품(Financial Product)", description = "예적금, 대출, 펀드 등 금융 상품 정보 관리 API")
@RestController
@RequestMapping(value = "/api/product")
@RequiredArgsConstructor
public class FinancialProductController {

    private final FinancialProductService financialProductService;

    @Operation(summary = "금융 상품 등록 (관리자용)", description = "새로운 금융 상품 정보를 등록합니다.")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> registerProduct(@RequestBody FinancialProductEntity product,
                                               HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        FinancialProductResult result = financialProductService.registerProduct(product, user);
        response.put("result", result.name());
        if (result == FinancialProductResult.SUCCESS) {
            response.put("productId", product.getProductId());
        }
        return response;
    }

    @Operation(summary = "금융 상품 목록 조회",
            description = "전체 또는 카테고리별(DEPOSIT/SAVINGS/LOAN/FUND) 금융 상품 목록을 페이지 단위로 조회합니다.")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getProductList(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Pair<FinancialProductResult, Pair<BoardPageVo, List<FinancialProductEntity>>> result =
                financialProductService.getProductList(category, page);
        response.put("result", result.getLeft().name());
        if (result.getLeft() == FinancialProductResult.SUCCESS) {
            response.put("page", result.getRight().getLeft());
            response.put("products", result.getRight().getRight());
        }
        return response;
    }

    @Operation(summary = "금융 상품 상세 조회", description = "특정 금융 상품의 상세 정보를 조회합니다.")
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getProductDetail(@PathVariable("productId") Integer productId,
                                                HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Pair<FinancialProductResult, FinancialProductEntity> result =
                financialProductService.getProductById(productId);
        response.put("result", result.getLeft().name());
        if (result.getLeft() == FinancialProductResult.SUCCESS) {
            response.put("product", result.getRight());
        }
        return response;
    }

    @Operation(summary = "금융 상품 정보 수정 (관리자용)",
            description = "기존 금융 상품의 정보를 수정합니다 (예: 이율 변경, 판매 상태 변경).")
    @PatchMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateProduct(@PathVariable("productId") Integer productId,
                                             @RequestBody FinancialProductEntity productInfo,
                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        productInfo.setProductId(productId);
        FinancialProductResult result = financialProductService.updateProduct(productInfo, user);
        response.put("result", result.name());
        return response;
    }

    @Operation(summary = "금융 상품 삭제 (관리자용)",
            description = "금융 상품을 비활성화합니다 (is_active = false 처리, 하드 삭제 아님).")
    @DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteProduct(@PathVariable("productId") Integer productId,
                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        FinancialProductResult result = financialProductService.deleteProduct(productId, user);
        response.put("result", result.name());
        return response;
    }

    @Operation(summary = "상담 중 금융 상품 신청",
            description = "행원이 상담 태스크 진행 중 고객의 금융 상품 신청을 처리합니다. " +
                    "task_processing_log에 상담 이력이 기록됩니다.")
    @PostMapping(value = "/{productId}/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> applyProduct(@PathVariable("productId") Integer productId,
                                            @RequestParam(value = "taskId") Long taskId,
                                            @RequestParam(value = "userId") Integer userId,
                                            @RequestParam(value = "amount", required = false) Long amount,
                                            @RequestParam(value = "durationMonths", required = false) Integer durationMonths,
                                            @RequestParam(value = "processingNote", required = false) String processingNote,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        MemberEntity member = (MemberEntity) session.getAttribute("member");
        FinancialProductResult result = financialProductService.applyProduct(
                productId, taskId, userId, member, amount, durationMonths, processingNote);
        response.put("result", result.name());
        return response;
    }
}
