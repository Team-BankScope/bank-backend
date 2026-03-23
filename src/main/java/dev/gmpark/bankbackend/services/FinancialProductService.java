package dev.gmpark.bankbackend.services;

import dev.gmpark.bankbackend.entities.FinancialProductEntity;
import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.ProductSubscriptionEntity;
import dev.gmpark.bankbackend.entities.TaskProcessingLogEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.enums.ProductCategory;
import dev.gmpark.bankbackend.mappers.FinancialProductMapper;
import dev.gmpark.bankbackend.results.FinancialProductResult;
import dev.gmpark.bankbackend.vos.BoardPageVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialProductService {

    private final FinancialProductMapper financialProductMapper;

    // 금융 상품 등록 (관리자 전용)
    public FinancialProductResult registerProduct(FinancialProductEntity product, UserEntity user) {
        if (user == null) {
            return FinancialProductResult.FAILURE_SESSION;
        }
        if (!"admin".equals(user.getUserType())) {
            return FinancialProductResult.FAILURE_UNAUTHORIZED;
        }
        if (product.getProductCategory() == null || product.getProductName() == null
                || product.getProductName().isBlank()) {
            return FinancialProductResult.FAILURE;
        }
        product.setIsActive(true);
        int rows = financialProductMapper.insertProduct(product);
        return rows > 0 ? FinancialProductResult.SUCCESS : FinancialProductResult.FAILURE;
    }

    /**
     * 금융 상품 목록 조회 (카테고리 필터 + 페이지네이션)
     * @param category ProductCategory 이름 문자열 (null 허용 → 전체 조회)
     * @return LEFT: 결과 코드 / RIGHT: Pair(페이지 정보, 상품 목록) — 실패 시 RIGHT=null
     */
    public Pair<FinancialProductResult, Pair<BoardPageVo, List<FinancialProductEntity>>> getProductList(
            String category, int requestPage) {
        // 카테고리 유효성 검사
        String normalizedCategory = null;
        if (category != null && !category.isBlank()) {
            try {
                ProductCategory.valueOf(category.toUpperCase());
                normalizedCategory = category.toUpperCase();
            } catch (IllegalArgumentException e) {
                return Pair.of(FinancialProductResult.FAILURE_INVALID_CATEGORY, null);
            }
        }

        int totalCount = financialProductMapper.countProducts(normalizedCategory);
        BoardPageVo pageVo = new BoardPageVo(requestPage, totalCount);
        List<FinancialProductEntity> products = financialProductMapper.selectProducts(
                normalizedCategory, pageVo.getRowCount(), pageVo.getDbOffset()
        );

        return Pair.of(FinancialProductResult.SUCCESS, Pair.of(pageVo, products));
    }

    /**
     * 금융 상품 단건 조회
     */
    public Pair<FinancialProductResult, FinancialProductEntity> getProductById(Integer productId) {
        FinancialProductEntity product = financialProductMapper.selectById(productId);
        if (product == null) {
            return Pair.of(FinancialProductResult.FAILURE_PRODUCT_NOT_FOUND, null);
        }
        return Pair.of(FinancialProductResult.SUCCESS, product);
    }

    /**
     * 금융 상품 정보 수정 (관리자 전용)
     */
    public FinancialProductResult updateProduct(FinancialProductEntity product, UserEntity user) {
        if (user == null) {
            return FinancialProductResult.FAILURE_SESSION;
        }
        if (!"admin".equals(user.getUserType())) {
            return FinancialProductResult.FAILURE_UNAUTHORIZED;
        }
        FinancialProductEntity existing = financialProductMapper.selectById(product.getProductId());
        if (existing == null) {
            return FinancialProductResult.FAILURE_PRODUCT_NOT_FOUND;
        }
        int rows = financialProductMapper.updateProduct(product);
        return rows > 0 ? FinancialProductResult.SUCCESS : FinancialProductResult.FAILURE;
    }

    /**
     * 금융 상품 비활성화 (소프트 삭제, 관리자 전용)
     */
    public FinancialProductResult deleteProduct(Integer productId, UserEntity user) {
        if (user == null) {
            return FinancialProductResult.FAILURE_SESSION;
        }
        if (!"admin".equals(user.getUserType())) {
            return FinancialProductResult.FAILURE_UNAUTHORIZED;
        }
        FinancialProductEntity existing = financialProductMapper.selectById(productId);
        if (existing == null) {
            return FinancialProductResult.FAILURE_PRODUCT_NOT_FOUND;
        }
        int rows = financialProductMapper.deactivateProduct(productId);
        return rows > 0 ? FinancialProductResult.SUCCESS : FinancialProductResult.FAILURE;
    }

    /**
     * 상담 중 금융 상품 신청 처리
     * 행원이 상담 태스크(IN_PROGRESS) 진행 중 상품 신청을 처리하면
     * task_processing_log에 이력을 남긴다.
     *
     * @param productId      신청할 상품 ID
     * @param taskId         해당업무(task)가 몇번째 번호인지(유저id도 내장)
     * @param member         담당 행원 세션
     * @param processingNote 행원이 작성한 상담 메모 (null 허용)
     */
    /**
     * @param userId         가입 고객 ID
     * @param amount         가입 금액 (null 허용)
     * @param durationMonths 가입 기간(개월) (null 허용)
     */
    public FinancialProductResult applyProduct(Integer productId, Long taskId,
                                               Integer userId, MemberEntity member,
                                               Long amount, Integer durationMonths,
                                               String processingNote) {
        if (member == null) {
            return FinancialProductResult.FAILURE_SESSION;
        }
        FinancialProductEntity product = financialProductMapper.selectById(productId);
        if (product == null || !Boolean.TRUE.equals(product.getIsActive())) {
            return FinancialProductResult.FAILURE_PRODUCT_NOT_FOUND;
        }

        // 고객 상품 가입 이력 저장
        ProductSubscriptionEntity sub = ProductSubscriptionEntity.builder()
                .userId(userId)
                .productId(productId)
                .taskId(taskId)
                .amount(amount)
                .durationMonths(durationMonths)
                .build();
        financialProductMapper.insertSubscription(sub);

        // 상담 처리 로그 기록
        String noteContent = "[금융상품 신청] " + product.getProductName()
                + " (" + product.getProductCategory().name() + ")"
                + (processingNote != null && !processingNote.isBlank() ? " | " + processingNote : "");

        TaskProcessingLogEntity log = TaskProcessingLogEntity.builder()
                .taskId(taskId)
                .memberId(member.getId().intValue())
                .actionType("ADD_NOTE")
                .processingNote(noteContent)
                .build();

        int rows = financialProductMapper.insertProcessingLog(log);
        return rows > 0 ? FinancialProductResult.SUCCESS : FinancialProductResult.FAILURE;
    }
}
