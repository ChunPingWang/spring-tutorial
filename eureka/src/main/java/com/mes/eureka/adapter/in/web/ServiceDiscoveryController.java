package com.mes.eureka.adapter.in.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceDiscoveryController {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public ServiceDiscoveryController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllServices() {
        List<String> services = discoveryClient.getServices();

        Map<String, Object> response = new HashMap<>();
        response.put("services", services);
        response.put("count", services.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<Map<String, Object>> getServiceInstances(@PathVariable String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);

        Map<String, Object> response = new HashMap<>();
        response.put("serviceId", serviceId);
        response.put("instances", instances.stream().map(instance -> {
            Map<String, Object> info = new HashMap<>();
            info.put("host", instance.getHost());
            info.put("port", instance.getPort());
            info.put("uri", instance.getUri());
            return info;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }
}
