package com.javalab.product.service;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.javalab.product.dto.OrderItemDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.entity.OrderItem;
import com.javalab.product.entity.Product;
import com.javalab.product.repository.OrderItemRepository;
import com.javalab.product.repository.ProductRepository;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
    							ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public PageResultDTO<OrderItemDTO, OrderItem> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("orderItemId").descending());
        Page<OrderItem> result = orderItemRepository.findAll(pageable);
        Function<OrderItem, OrderItemDTO> fn = this::entityToDto;
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public OrderItemDTO read(Integer orderItemId) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(orderItemId);
        return orderItem.map(this::entityToDto).orElse(null);
    }

    @Override
    public OrderItem register(OrderItemDTO orderItemDTO) {
        OrderItem entity = dtoToEntity(orderItemDTO);
        return orderItemRepository.save(entity);
    }

    @Override
    public void modify(OrderItemDTO orderItemDTO) {
    	// 1. 주문 item 조회
        Optional<OrderItem> orderItem = orderItemRepository.findById(orderItemDTO.getOrderItemId());
        // 2. 존재하면
        orderItem.ifPresent(existingOrderItem -> {
            // 3. DB에서 조회한 객체를 화면에서 입력한 정보로 다시 세팅
        	Product product = productRepository.findById(orderItemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));
            existingOrderItem.setProduct(product);
            existingOrderItem.setQuantity(orderItemDTO.getQuantity());
            orderItemRepository.save(existingOrderItem);
        });
    }


    @Override
    public boolean remove(Integer orderItemId) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(orderItemId);
        if (orderItem.isPresent()) {
            orderItemRepository.deleteById(orderItemId);
            return true;
        } else {
            return false;
        }
    }

//    public OrderItemDTO entityToDto(OrderItem orderItem) {
//        return OrderItemDTO.builder()
//                .orderItemId(orderItem.getOrderItemId())
//                .productId(orderItem.getProduct().getProductId())
//                .quantity(orderItem.getQuantity())
//                .build();
//    }
//
//    public OrderItem dtoToEntity(OrderItemDTO orderItemDTO) {
//        return OrderItem.builder()
//                .orderItemId(orderItemDTO.getOrderItemId())
//                .product(orderItemDTO.getProduct())
//                .quantity(orderItemDTO.getQuantity())
//                .build();
//    }
}
