package com.mes.common.exception;

/**
 * 當查找的 Entity 不存在時拋出。
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String id) {
        super(String.format("%s not found with id: %s", entityName, id));
    }
}
