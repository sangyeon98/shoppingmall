package com.javalab.product.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_order_master")
public class OrderMaster extends BaseEntity {

	// 주문번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 10)
    private Integer orderId;

    // 배송지 주소
    //@NotNull
    //@Size(min = 10, max = 100, message = "배송 주소는 10~100자입니다")
    @Column(length = 100, nullable = false)
    private String address;

    // 주문 회원
    @ManyToOne
    @JoinColumn(name = "email")
    private Member member;
    
    // 주문 상품들
    @OneToMany(mappedBy = "orderMaster", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // 총 주문금액
    @Builder.Default
    private Integer totalAmt = 0;
}
