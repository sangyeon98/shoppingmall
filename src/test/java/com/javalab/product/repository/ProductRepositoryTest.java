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
import com.javalab.product.entity.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Product 엔티티 관련 Repository 단위 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Slf4j
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // 카테고리 레파지토리가 필요해서 의존성을 주입함.
    // 상품 저장시 실제로 있는 카테고리를 넣기 위해서 조회할 때 필요함.
    @Autowired
    private CategoryRepository categoryRepository;
    
    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryTest.class);
    
    
	/*
	 * Product 더미 데이터 저장
	 *  - Validation 조건을 체크하고 그에 맞게 샘플 데이터를 입력할것.
	 *    조건에 맞지 않으면 오류나며 원인을 찾기 힘듦.
	 */
    
    /*
	//@Test
	//@Disabled
	@Commit	// 기본은 롤백
	public void testProductSave() {
		
		// 상품 등록시 사용할 실제로 존재하는 카테고리 객체 조회
		Integer categoryId = 1;
	    Category category = categoryRepository.findById(categoryId).orElseThrow(); // Retrieve an existing Category entity from the database

		IntStream.rangeClosed(1, 100).forEach(i -> {
			Product product = new Product();
			product.setProductName("Product Name" + i);
			product.setPrice(120000);
			product.setCategory(category);
			product.setDescription("상품 설명" + i);
			product.setRegDate(LocalDateTime.now());
			
			// 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
			productRepository.save(product);	
		});
		
		log.info("Product 저장 완료!");	
	}
	*/
    
    /*
     * @Builder 패턴 적용
     */
	//@Test
	//@Disabled
	@Commit	// 기본은 롤백
    public void testProductSave() {
        // 상품 등록시 사용할 실제로 존재하는 카테고리 객체 조회
        Integer categoryId = 1;
        Category category = categoryRepository.findById(categoryId).orElseThrow(); 

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Product product = Product.builder()
                    .productName("Product Name" + i)
                    .price(120000)
                    .category(category)
                    .description("상품 설명" + i)
                    .build();

            // 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
            productRepository.save(product);
        });

        log.info("Product 저장 완료!");
    }
    
    
	
	/*
	 * Product 목록 조회(페이지네이션/정렬 기능 구현)
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
	public void testProductPagingSorting() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("productId").descending());
		Page<Product> result = productRepository.findAll(pageable);
		result.stream().forEach(c -> log.info(c.toString()));
	}
	
	/* 
	 *  Product 한개 조회
	 *  
	 *  [Fetch 전략 - Eager / Lazy]
	 *   - Fetch 전략을 Lazy로 하게 되면 지연로딩이 되어 처음에는 상품 테이블만 조회하고
	 *     카테고리 테이블이 필요한 시점 즉, product.toString()이 되는 시점에
	 *     카테고리 테이블에 조회해서 카테고리 관련 모든 정보를 조회한다.
	 *   - Fetch 전략을 안주거나, Eager로 하게 되면 처음 부터 상품 테이블과
	 *     카테고리 테이블을 조인걸게 된다. 그렇게 되면 데이터베이스에 성능에
	 *     좋지 않다. 가급적이면 지연로딩을 사용하는게 낫다.
	 */
	//@Test
	//@Disabled
	public void testGetProductById() {
		Optional<Product> optProduct = productRepository.findById(1); // 데이터베이스 확인
		Product product = optProduct.get();
		log.info("Product의 이름 조회 : " + product.getProductName());
		// 1. 상품 엔티티의 Category 멤버 변수에 지연로딩을 설정해놓게 되면 
		//    이 시점까지는 상품 테이블만 조회함.
		// 2. 아래의 product.toString()으로 상품 정보의 모든 내용(Category포함)
		//    보겠다고 하는 시점에 그제서야 카테고리 테이블에 조회를 한다.
		log.info("Product 전체 정보 조회 : " + product.toString());
		
	}		

	/* 
	 *  Product 수정
	 */
	//@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testProductUpdate() {
		
		// 상품 등록시 사용할 실제로 존재하는 카테고리 객체 조회
		Integer categoryId = 9;
	    Category category = categoryRepository.findById(categoryId).orElseThrow(); // 실제로 존재하는 카테고리
		
	    // 업데이트 할 때는 Validation조건에 맞도록 할것.
		Product product = new Product();
		product.setProductId(1);	// 실존하는 상품Id로
		product.setProductName("변경된 상품명입니다."); // 반드시 8~50자 사이로 입력
		product.setPrice(325000);
		product.setDescription("변경된 상품 설명입니다.");
		product.setCategory(category);	// 실존하는 카테고리 객체
		
		// 영속화 - 엔티티 매니저가 관리하는 메모리로 저장됨
		productRepository.save(product);
		
		log.info("수정 완료!");	
	}
	
	/* 
	 *  Product 삭제
	 */
	//@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testProductDelete() {
		
		productRepository.deleteById(10); // 실제로 있는 Product ID
		
		log.info("삭제 완료!");	
	}		
	
	/*
	 * 상품과 카테고리 정보를 동시에 조회
	 */
    //@Test
    public void testReadWithWriter() {
        Object result = productRepository.getProductWithCategory(1);
        Object[] arr = (Object[])result;
        //log.info("-------------------------------");
        //log.info(Arrays.toString(arr));

    }	
	
	
}
