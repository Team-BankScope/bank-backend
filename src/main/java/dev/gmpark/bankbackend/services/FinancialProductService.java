package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.FinancialProductEntity;
import dev.gmpark.bankbackend.results.CommonResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialProductService {

    public CommonResult registerProduct(FinancialProductEntity product) {
        return null;
    }

    public List<FinancialProductEntity> getProductList(String category) {
        return null;
    }

    public FinancialProductEntity getProductById(Integer productId) {
        return null;
    }

    public CommonResult updateProduct(FinancialProductEntity productInfo) {
        return null;
    }

    public CommonResult deleteProduct(Integer productId) {
        return null;
    }
}
