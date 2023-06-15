package com.javalab.product.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.repository.CategoryRepository;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class CategoryControllerTest {

	    @Autowired
	    private CategoryController categoryController;

	    @Autowired
	    private CategoryRepository categoryRepository;

	    //@Test
	    public void testGetAllCategories() {
//	        // Create a PageRequestDTO with appropriate parameters
//	        PageRequestDTO pageRequestDTO = new PageRequestDTO();
//	        pageRequestDTO.setPage(1);
//	        pageRequestDTO.setSize(10);
//
//	        // Call the getAllCategories method of the categoryController
//	        String viewName = categoryController.getList(pageRequestDTO);
//
//	        // Verify the results
//	        assertThat(viewName).isEqualTo("categoryList");
//	        assertThat(model.containsAttribute("categoryList")).isTrue();
	    }

	    // Similarly, you can create unit tests for other methods in the CategoryController

	}
