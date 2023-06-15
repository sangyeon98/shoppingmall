package com.javalab.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
	[@EnableJpaAuditing]
	엔티티의 자동 감사를 활성화하기 위해 Spring Data JPA에서 
	두 개의 주석 `@EntityListeners` 및 `@EnableJpaAuditing`이 
	사용. (JPA 엔티티 감시하는 역할의 어노테이션)
	
	구성 클래스에 `@EnableJpaAuditing`이 추가되면 Spring Data JPA
	에서 제공하는 감사 인프라를 활성화합니다.
	
	`@EntityListeners`로 지정된 엔티티 리스너가 감사 목적으로 등록 
	 및 호출되도록 합니다.
	- `createdBy`, `createdDate`, `lastModifiedBy` 및 
	`lastModifiedDate`와 같은 엔티티의 감사 관련 필드를 자동으로 
	 채우도록 필요한 인프라를 구성
*/
@SpringBootApplication
@EnableJpaAuditing
public class Chap63JpaProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(Chap63JpaProductApplication.class, args);
	}

}
