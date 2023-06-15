package com.javalab.product.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.javalab.product.entity.Category;
import com.javalab.product.entity.Product;

/*
 * [Product 레파지토리 인터페이스]
 *  - 다음 코드로 작성만 해놓으면 내부적으로 C/R/U/D 메소드가 
 *    자동으로 구현되어진다.
 *  - JpaRepository<Product, Integer> : 
 *  	- Product : 엔티티의 타입(클래스)이름, 
 *    	- Integer : 키 컬럼의 자료형(wrapper) 타입
 *  - 추가로 필요한 메소드는 정의해서 사용 가능.    
 */
public interface ProductRepository extends JpaRepository<Product, Integer>{

	/*
	 * 연관관계가 있는 엔티티를 조인해서 동시에 상품정보와 카테고리 정보 추출
	 * 상품 테이블에는 카테고리ID만 있고 카테고리 이름이 없다. 그래서 동시에 
	 * 상품정보와 카테고리 정보를 보려면 다음과 같이 JPQL 쿼리를 문을 작성해야 한다.
	 * - p.category c : 카테고리 테이블에 있는 카테고리 객체를 지칭함.
	 * - left join : 뒤에 On 이 없다.
	 */
    @Query("select p, c from Product p left join p.category c where p.productId = :productId")
    Object getProductWithCategory(@Param("productId") Integer productId);
    
}