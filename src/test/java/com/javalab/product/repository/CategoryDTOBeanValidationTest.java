package com.javalab.product.repository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.javalab.product.dto.CategoryDTO;

@SpringJUnitConfig
public class CategoryDTOBeanValidationTest {
    private static LocalValidatorFactoryBean validator;

    @BeforeAll
    public static void setupValidator() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
    }

    //@Test
    public void testValidCategoryDTO() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryName("ValidCat")
                .description("1234567890")
                .build();

        assertTrue(validator.validate(categoryDTO).isEmpty());
    }
    
    //@Test
    public void testValidNotNullEmptyBlank() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryName(" ")
                .description(" ")
                .build();

        assertTrue(validator.validate(categoryDTO).isEmpty());
    }

    /*


    // @Test
    public void testInvalidCategoryName() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryName("1234567") // 8자가 안되게 입력
                .description("12345678901")
                .build();

        assertFalse(validator.validate(categoryDTO).isEmpty());
    }

    //@Test
    public void testEmptyCategoryName() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .description("This category has an empty name.")
                .build();

        assertFalse(validator.validate(categoryDTO).isEmpty());
    }

    //@Test
    public void testInvalidDescription() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append("a");
        }

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .categoryName("Valid Category")
                .description(sb.toString())
                .build();

        assertFalse(validator.validate(categoryDTO).isEmpty());
    }
    */
}

