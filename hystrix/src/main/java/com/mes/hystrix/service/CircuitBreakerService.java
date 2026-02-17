package com.mes.hystrix.service;

import org.springframework.stereotype.Service;

@Service
public class CircuitBreakerService {

    public String callWithCircuitBreaker(String id) {
        if ("fail".equals(id)) {
            throw new RuntimeException("Service unavailable");
        }
        return "Success: " + id;
    }

    public String fallback(String id, Throwable t) {
        return "Fallback response for: " + id;
    }
}
