package com.javalab.product.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * [값 검증 + 화면에서 입력된 값 보관]
 * 
 * DTo 클래스
 *  - 화면에서 입력된 값을 받아서 보관하는 클래스
 *  - 또한 화면에서 전달된 값을 검증하는데 필요한 어노테이션이 정의되어 있음
 *  - 화면의 파라미터 바인딩, 값을 검증하는 역할
 *  
 * Validation 
 *  - Bean Validation API, 스프링에서 제공해주는
 *    검증 API를 통해서 각 값들이 정상적으로 입력되었는지
 *    검증한다.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

	private Integer categoryId;
	
    @NotNull(message = "카테고리명을 입력하세요") // "" " "통과
    @Size(min = 8, max = 50, message = "카테고리명은 문자 8자~50자 사이로 입력하세요")
    private String categoryName;

    // 이 부분은 수정하고 테스트!
    @NotEmpty // 빈칸도 허용 안됨. ""안됨 " " 통과됨.
    //@NotBlank // null, 빈칸, " " 모두 허용 안됨. 가장 강력
    @Size(max = 100, message = "카테고리 설명은 100자를 넘을 수 없습니다.")
    private String description;

	// @Builder.Default 이 어노테이션이 있어야 @Builder에 의해서 
	// 초기화 조건이 무시되지 않음.
    @Builder.Default
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate = LocalDateTime.now();
    
    // 카테고리 드롭다운 리스트에서 자동으로 선택될 수 있도록 하기 위한 멤버 변수.
    private boolean isSelected;
}
