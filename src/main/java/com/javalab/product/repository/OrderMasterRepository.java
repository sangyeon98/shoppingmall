package com.javalab.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javalab.product.entity.OrderMaster;

public interface OrderMasterRepository extends JpaRepository<OrderMaster, Integer> {
    // Add custom query methods if needed
}