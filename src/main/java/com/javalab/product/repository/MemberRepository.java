package com.javalab.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javalab.product.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    // Add custom query methods if needed
}
