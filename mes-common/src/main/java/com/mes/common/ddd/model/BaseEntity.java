package com.mes.common.ddd.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * [DDD Pattern: Entity 基礎類別]
 * [SOLID: SRP - 只負責 Entity 的共通行為（身份識別、時間戳記）]
 *
 * Entity 的核心特性是具有唯一識別 (Identity)。
 * 兩個 Entity 即使所有屬性相同，只要 ID 不同就視為不同的物件。
 *
 * @param <ID> 識別值的型別
 */
public abstract class BaseEntity<ID> {

    private ID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected BaseEntity() {
    }

    protected BaseEntity(ID id) {
        this.id = Objects.requireNonNull(id, "Entity ID must not be null");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public ID getId() {
        return id;
    }

    protected void setId(ID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    protected void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Entity 的相等性由 ID 決定，而非屬性值。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
