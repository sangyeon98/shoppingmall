package com.javalab.product.service;

import java.util.List;

import com.javalab.product.dto.MemberDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;

public interface MemberService {

    PageResultDTO<MemberDTO, Member> getList(PageRequestDTO requestDTO);
    List<MemberDTO> getList();
    MemberDTO read(String email);
    Member register(MemberDTO memberDTO);
    void modify(MemberDTO memberDTO);
    boolean remove(String email);

    
    //컨트롤러에서 넘어돈 DTO를 Entity로 바꿔주는 역할 
    default Member dtoToEntity(MemberDTO memberDTO) {
        return Member.builder()
                .email(memberDTO.getEmail())
                .password(memberDTO.getPassword())
                .address(memberDTO.getAddress())
                .name(memberDTO.getName())
                .build();
    }

    //데이터베이스에서 받아온 Entity를 DTO로 바꿔주는 역할
    default MemberDTO entityToDto(Member member) {
        return MemberDTO.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .address(member.getAddress())
                .build();
    }
}
