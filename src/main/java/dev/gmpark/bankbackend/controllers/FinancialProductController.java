package dev.gmpark.bankbackend.controllers;


import dev.gmpark.bankbackend.entities.FinancialProductEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.services.FinancialProductService; 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public Map<String, Object> registerProduct(@RequestBody FinancialProductEntity product, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }
        
        // 권한 체크: 관리자(admin) 또는 특정 권한을 가진 행원만 등록 가능하도록 가정
        if (!"admin".equals(user.getUserType())) {
            response.put("result", "FAILURE_UNAUTHORIZED");
            return response;
        }

        // TODO: FinancialProductService를 호출하여 상품 등록 로직 처리
        // CommonResult result = this.financialProductService.registerProduct(product);
        // response.put("result", result.name());
        
        response.put("result", "SUCCESS"); // 임시 응답
        return response;
    }

    @Operation(summary = "금융 상품 목록 조회", description = "전체 또는 카테고리별 금융 상품 목록을 조회합니다.")
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getProductList(
            @RequestParam(value = "category", required = false) String category,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();

        // TODO: FinancialProductService를 호출하여 상품 목록 조회 (category 조건 포함)
        // List<FinancialProductEntity> products = this.financialProductService.getProductList(category);
        // response.put("result", "SUCCESS");
        // response.put("products", products);
        
        response.put("result", "SUCCESS"); // 임시 응답
        return response;
    }

    @Operation(summary = "금융 상품 상세 조회", description = "특정 금융 상품의 상세 정보를 조회합니다.")
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getProductDetail(@PathVariable("productId") Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // TODO: FinancialProductService를 호출하여 특정 상품 조회
        // FinancialProductEntity product = this.financialProductService.getProductById(productId);
        // if (product != null) {
        //     response.put("result", "SUCCESS");
        //     response.put("product", product);
        // } else {
        //     response.put("result", "FAILURE_NOT_FOUND");
        // }
        
        response.put("result", "SUCCESS"); // 임시 응답
        return response;
    }

    @Operation(summary = "금융 상품 정보 수정 (관리자용)", description = "기존 금융 상품의 정보를 수정합니다 (예: 이율 변경, 판매 상태 변경).")
    @PatchMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateProduct(
            @PathVariable("productId") Integer productId, 
            @RequestBody FinancialProductEntity productInfo, 
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }
        
        // 권한 체크: 관리자(admin)만 수정 가능
        if (!"admin".equals(user.getUserType())) {
            response.put("result", "FAILURE_UNAUTHORIZED");
            return response;
        }

        // TODO: FinancialProductService를 호출하여 상품 정보 수정
        // productInfo.setProductId(productId);
        // CommonResult result = this.financialProductService.updateProduct(productInfo);
        // response.put("result", result.name());
        
        response.put("result", "SUCCESS"); // 임시 응답
        return response;
    }

    @Operation(summary = "금융 상품 삭제 (관리자용)", description = "금융 상품을 삭제합니다. (실제로는 삭제보단 is_active=false 처리를 권장)")
    @DeleteMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> deleteProduct(@PathVariable("productId") Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        if (user == null) {
            response.put("result", "FAILURE_SESSION");
            return response;
        }
        
        // 권한 체크: 관리자(admin)만 삭제 가능
        if (!"admin".equals(user.getUserType())) {
            response.put("result", "FAILURE_UNAUTHORIZED");
            return response;
        }

        // TODO: FinancialProductService를 호출하여 상품 삭제 또는 비활성화
        // CommonResult result = this.financialProductService.deleteProduct(productId);
        // response.put("result", result.name());
        
        response.put("result", "SUCCESS"); // 임시 응답
        return response;
    }
}
