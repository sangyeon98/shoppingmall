package com.javalab.product.repository;

import static org.junit.jupiter.api.Assertions.*;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.javalab.product.entity.Category;

public class CategoryValidationTest {
	
    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    //@Test
    public void testValidCategory() {
        Category category = Category.builder()
                .categoryName("Valid Category")
                .description("This is a valid category.")
                .build();

        assertTrue(validator.validate(category).isEmpty());
    }

    //@Test
    public void testInvalidCategoryName() {
        Category category = Category.builder()
                .categoryName("Short")
                .description("This category name is too short.")
                .build();

        assertFalse(validator.validate(category).isEmpty());
    }

    //@Test
    public void testNullCategoryName() {
        Category category = Category.builder()
                .description("This category has a null name.")
                .build();

        assertFalse(validator.validate(category).isEmpty());
    }
}
