package com.javalab.product.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javalab.product.dto.OrderItemDTO;
import com.javalab.product.dto.OrderMasterDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;
import com.javalab.product.entity.OrderItem;
import com.javalab.product.entity.OrderMaster;
import com.javalab.product.repository.OrderItemRepository;
import com.javalab.product.repository.OrderMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
//@Transactional
@Slf4j
public class OrderMasterServiceImpl implements OrderMasterService {

    private final OrderMasterRepository orderMasterRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderMasterServiceImpl(OrderMasterRepository orderMasterRepository,
    							OrderItemRepository orderItemRepository) {
        this.orderMasterRepository = orderMasterRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public PageResultDTO<OrderMasterDTO, OrderMaster> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("orderId").descending());
        Page<OrderMaster> result = orderMasterRepository.findAll(pageable);
        Function<OrderMaster, OrderMasterDTO> fn = this::entityToDto;
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public OrderMasterDTO read(Integer orderId) {
        Optional<OrderMaster> orderMaster = orderMasterRepository.findById(orderId);
        return orderMaster.map(this::entityToDto).orElse(null);
    }

    /*
     * 저장
     * @Transactional 
     *  : 마스터 저장후 아이템 저장을 하나의 트랜잭션으로 묶음
     *   저장하다가 중간에 튕기면 데이터 다 롤백하기 위한거다.
     */
    @Override
    @Transactional
    public void register(OrderMasterDTO orderMasterDTO) {
    	
        // [1]OrderMasterDTO DTO -> OrderMaster Entity 변환
        OrderMaster orderMaster = dtoToEntity(orderMasterDTO);

        log.info("ServiceImpl register : " + orderMaster.toString());
        
        // 우선 OrderMaster 저장, save = jparepository가 가지고 있는거
        OrderMaster savedOrderMaster = orderMasterRepository.save(orderMaster);

        // OrderItemDTO -> OrderItem Entity 변환 및 연결
        List<OrderItem> orderItems = orderMasterDTO.getOrderItems().stream()
                .map(item -> {
                    OrderItem orderItem = orderItemDtoToEntity(item);
                    // 저장된 엔티티를 할당, 거기에 orderId 있음, 
                    // 그 orderId가 orderItem테이블에 들어감
                    orderItem.setOrderMaster(savedOrderMaster); 
                    return orderItem;
                }).collect(Collectors.toList());

        // OrderItem들 저장
        orderItemRepository.saveAll(orderItems);
    }
    
    /* 
     * 주문 마스터 수정
     */
    @Override
    public void modify(OrderMasterDTO orderMasterDTO) {
        Optional<OrderMaster> orderMaster = orderMasterRepository.findById(orderMasterDTO.getOrderId());

        // 화면에서 전달된 이메일(pk)로 회원 정보 테이블에서 회원 조회
        Member member = Member.builder().email(orderMasterDTO.getEmail()).build();

        orderMaster.ifPresent(orderMasterEntity -> {
            OrderMaster updatedOrderMaster = dtoToEntity(orderMasterDTO);
            updatedOrderMaster.setMember(member);
            orderMasterRepository.save(updatedOrderMaster);
        });
    }
    
    @Override
    public boolean remove(Integer orderId) {
        Optional<OrderMaster> orderMaster = orderMasterRepository.findById(orderId);
        if (orderMaster.isPresent()) {
            orderMasterRepository.deleteById(orderId);
            return true;
        } else {
            return false;
        }
    }

}
