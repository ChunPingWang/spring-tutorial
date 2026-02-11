package com.mes.boot.workorder.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object]
 * [SOLID: SRP - 只負責封裝產品相關的描述性資訊]
 *
 * 產品資訊值物件，包含產品代碼、名稱與規格。
 * 作為 Value Object，ProductInfo 是不可變的，且相等性由所有屬性值共同決定。
 *
 * 在工單聚合中，ProductInfo 描述了該工單要生產的產品。
 */
@ValueObject
public final class ProductInfo extends BaseValueObject {

    private final String productCode;
    private final String productName;
    private final String specification;

    public ProductInfo(String productCode, String productName, String specification) {
        Objects.requireNonNull(productCode, "Product code must not be null");
        Objects.requireNonNull(productName, "Product name must not be null");

        if (productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code must not be empty");
        }
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name must not be empty");
        }

        this.productCode = productCode;
        this.productName = productName;
        this.specification = specification;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getSpecification() {
        return specification;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(productCode, productName, specification);
    }
}
