package com.javalab.product.repository;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Commit;

import com.javalab.product.entity.Category;
import com.javalab.product.entity.Member;
import com.javalab.product.entity.Product;

//import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Product 엔티티 관련 Repository 단위 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Slf4j
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryTest.class);

    /*
     * @Builder 패턴 적용
     */
    //@Test
    @Commit
    public void insertMembers() {

        IntStream.rangeClosed(1, 10).forEach(i -> {

            Member member = Member.builder()
                    .email("user" + i + "@aaa.com")
                    .password("1")
                    .address("address")
                    .name("회원님" + i)
                    .build();

            memberRepository.save(member);
        });
    }
    	
}
