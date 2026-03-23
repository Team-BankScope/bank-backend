package dev.gmpark.bankbackend.mappers;

import dev.gmpark.bankbackend.entities.FinancialProductEntity;
import dev.gmpark.bankbackend.entities.ProductSubscriptionEntity;
import dev.gmpark.bankbackend.entities.TaskProcessingLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FinancialProductMapper {

    // 상품 등록
    int insertProduct(@Param("product") FinancialProductEntity product);

    // 상품 목록 조회 (카테고리 필터 + 페이지네이션)
    List<FinancialProductEntity> selectProducts(
            @Param("category") String category,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 상품 전체 건수 (카테고리 필터 포함)
    int countProducts(@Param("category") String category);

    // 상품 단건 조회
    FinancialProductEntity selectById(@Param("productId") Integer productId);

    // 상품 정보 수정
    int updateProduct(@Param("product") FinancialProductEntity product);

    // 상품 비활성화 (소프트 삭제)
    int deactivateProduct(@Param("productId") Integer productId);

    // 상담 처리 로그 기록 (금융상품 신청 시)
    int insertProcessingLog(@Param("log") TaskProcessingLogEntity log);

    // 고객 상품 가입 이력 저장
    int insertSubscription(@Param("sub") ProductSubscriptionEntity sub);
}
