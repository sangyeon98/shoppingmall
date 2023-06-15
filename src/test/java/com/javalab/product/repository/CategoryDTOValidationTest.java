package com.javalab.product.repository;

import static org.junit.jupiter.api.Assertions.*;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



import com.javalab.product.dto.CategoryDTO;

public class CategoryDTOValidationTest {
    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //@Test
    public void testValidCategoryDTO() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryName("ValidCat")
                .description("1234567890")
                .build();

        assertTrue(validator.validate(categoryDTO).isEmpty());
    }
    
    /*
     * @NotNull, @NotEmpty, @NotBlank 비교 
     *  - @NotNull : 값만 들어오면 통과 ""도 통과
     *  - @NotEmpty : " "면 통과
     *  - @NotBlank : null, "", " " 모두 불가 
     */
    //@Test
    public void testValidNotNullEmptyBlank() {
    	CategoryDTO categoryDTO = CategoryDTO.builder()
    			.categoryName(" ")
    			.description(" ")
    			.build();
    	
    	assertTrue(validator.validate(categoryDTO).isEmpty());
    }
}
