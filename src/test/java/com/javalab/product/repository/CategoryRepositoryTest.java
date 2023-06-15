package com.javalab.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
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

//import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 카테고리 엔티티 관련 Repository 단위 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Slf4j
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private static final Logger log = LoggerFactory.getLogger(CategoryRepository.class);

    
	/*
	 * 카테고리 더미 데이터 저장
	 * In Java, the default value for the LocalDateTime type is null.
	 * 
	 */
	//@Test
	//@Disabled
	@Commit	// 기본은 롤백
	public void testCategoryRegister() {
		IntStream.rangeClosed(1, 10).forEach(i -> {
			Category category = new Category();
			category.setCategoryName("Category" + i);
			category.setDescription("Category 설명" + i);
			// 날짜는 넣지 않아도 BaseEntity에 설정으로 자동 입력됨.
			//category.setRegDate(LocalDateTime.now());
			
			// 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
			categoryRepository.save(category);	
		});
		log.info("Category 저장 완료!");	
	}
	
	/*
	 * 카테고리 더미 데이터 저장[빌더 패턴]
	 */
	//@Test
	//@Disabled
	@Commit	// 기본은 롤백
	public void testCategoryRegisterBuilder() {
		IntStream.rangeClosed(1, 100).forEach(i -> {
			Category category =	Category.builder()
								.categoryName("Category Builder" + i)
								.description("Category  Builder 설명" + i).build();
					
			// 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
			categoryRepository.save(category);	
		});
		log.info("Category 저장 완료!");	
	}
	
	/*
	 * 카테고리 목록 조회(페이지네이션/정렬 기능 구현)
	 *  Spring Data Jpa의 페이징, 정렬은 findAll(pageable)메소드 사용
	 *   - findAll(Pageable) : PagingAndSortRepository 소속 메소드
	 *   - Page<T> : 반환타입이 Page<T> 타입인 경우 에는 인자가 반드시 Pageable이어야 함.
	 *   - Pageable : 페이징 관련 정보를 담고 있는 객체가 구현해야 할 인터페이스.
	 *     어떻게 페이징과 정렬을 해야 하는지에 대한 정보가 있다.
	 *   - PageRequest : Pageable 인터페이스를 구현한 구현체(요청페이지, 게시물수, 정렬) 
	 *   - static of() : PageRequest 객체를 얻기 위해서 필요한 정적 메소드
	 *   - Sort.by("id") : 정렬 컬럼(필드)
	 */
	//@Test
	//@Disabled
	public void testCategoryPagingSorting() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryId").descending());
		Page<Category> result = categoryRepository.findAll(pageable);
		result.stream().forEach(c -> log.info(c.toString()));
	}
	
	/* 
	 *  카테고리 한개 조회
	 */
	//@Test
	//@Disabled
	public void testGetCategoryById() {
		Optional<Category> optCategory = categoryRepository.findById(1); // 데이터베이스 확인
		Category category = optCategory.get();
		log.info("카테고리 한개 조회 완료 : " + category.toString());
	}		

	/* 
	 *  카테고리 수정
	 */
	//@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testCategoryUpdate() {

		Category category = new Category();
		category.setCategoryId(1); // 수정할 카테고리 ID 지정
		category.setCategoryName("Category" + "변경됨");
		category.setDescription("변경된 Category");
		// 영속화 - 엔티티 매니저가 관리하는 메모리로 저장됨
		categoryRepository.save(category);
		
		log.info("수정 완료!");	
	}
	
	/* 
	 *  카테고리 삭제
	 */
	//@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testCategoryDelete() {
		
		categoryRepository.deleteById(10); // 실제로 있는 카테고리 ID
		
		log.info("삭제 완료!");	
	}		
	
}
