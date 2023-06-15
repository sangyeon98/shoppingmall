package com.javalab.product.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.javalab.product.entity.Category;

/*
 * [카테고리 레파지토리 인터페이스]
 *  - 다음 코드로 작성만 해놓으면 내부적으로 C/R/U/D 메소드가 
 *    자동으로 구현되어진다.
 *  - JpaRepository<Category, Integer> : 
 *  	- Category : 엔티티의 타입(클래스)이름, 
 *    	- Integer : 키 컬럼의 자료형(wrapper) 타입
 *  - 추가로 필요한 메소드는 정의해서 사용 가능.    
 */
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	
}