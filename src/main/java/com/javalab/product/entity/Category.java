package com.javalab.product.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 카테고리 엔티티 클래스
 *  - EntityManager에 의해서 관리되는 클래스
 *  @Id	어노테이션은 import javax.persistence.Id; [주의]
 *  @Builder : 빌더 패턴 구현, 롬복에서 제공해주는 기능을
 *  	객체 생성시 builder()메소드를 통해서 빈객체를 생성하고
 *  	그 빈 객체에 메소드 체이닝(쩜 찍어서 연결)형태로 객체에
 *  	값을 저장할 수 있다. 이때 멤버 변수 이름에다 직접 값을
 *  	넣을 수 있어서 직관적이다. 아주 편리함.
 */
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_category")
public class Category  extends BaseEntity{

	/*
	 * @Id : @Id 어노테이션이 붙은 엔티티의 필드를 테이블의 기본키(PK)와 매핑 
	 * strategy : 기본 키 생성 전략으로 AUTO/IDENTITY/SEQUENCE /TABLE 옵션이 있음
	 *  - AUTO : DB 종류에 따라 JPA가 알맞은 것을 선택
	 *  - IDENTITY : 기본 키 생성을 데이터베이스에 위임
	 *  - SEQUENCE : 데이터베이스 시퀀스를 사용해서 기본 키를 할당
	 *  - TABLE : 키 생성 전용 테이블을 만들어서 sequence처럼 사용
	 *  
	 *  날짜는 자동으로 넣어주는거 BaseEntity에서 상속받아옴
	 *  
	 *  notnull, size(validation하는것들은 dto에서 해도 된다.
	 */
	@Id	// import javax.persistence.Id;
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(length=10)
	private Integer categoryId;
	
	@NotNull
	@Size(min=8, 
		  max=50, 
		  message="카테고리명은 8~50자입니다")
	@Column(length = 50, nullable=false)
	private String categoryName;
	
	@Column(length = 100)
	private String description;
		
	//@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	//private LocalDateTime regDate = LocalDateTime.now(); // 현재일자
}
