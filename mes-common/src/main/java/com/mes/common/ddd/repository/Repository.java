package com.mes.common.ddd.repository;

import com.mes.common.ddd.model.BaseAggregateRoot;

import java.util.List;
import java.util.Optional;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 只定義聚合根的基本 CRUD 操作]
 * [SOLID: DIP - 領域層定義介面，基礎設施層實作]
 * [Hexagonal Architecture: 這是一個 Output Port]
 *
 * Repository 提供類似集合 (Collection) 的介面來存取聚合根，
 * 隱藏底層的持久化細節（記憶體、資料庫、檔案等）。
 *
 * 每個聚合根應有對應的 Repository 介面，定義在 domain 層中。
 *
 * @param <T>  聚合根的型別
 * @param <ID> 聚合根識別碼的型別
 */
public interface Repository<T extends BaseAggregateRoot<ID>, ID> {

    /**
     * 根據 ID 查找聚合根。
     */
    Optional<T> findById(ID id);

    /**
     * 查找所有聚合根。
     */
    List<T> findAll();

    /**
     * 儲存聚合根（新增或更新）。
     */
    void save(T aggregate);

    /**
     * 根據 ID 刪除聚合根。
     */
    void deleteById(ID id);
}
