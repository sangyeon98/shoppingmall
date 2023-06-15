package com.javalab.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javalab.product.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // Add custom query methods if needed
}
