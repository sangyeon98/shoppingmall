package com.javalab.product.service;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.javalab.product.dto.CategoryDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	// 의존성주입 - CategoryRepository
    private final CategoryRepository categoryRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 카테고리 목록 조회[페이징]
    @Override
    public PageResultDTO<CategoryDTO, Category> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("categoryId").descending());
        
        Page<Category> result = categoryRepository.findAll(pageable);
        
        Function<Category, CategoryDTO> fn = (entity -> entityToDto(entity)); // java.util.Function
        return new PageResultDTO<>(result, fn );
    }    
    
    // 카테고리 목록 조회[페이징 없음]
    @Override
    public List<CategoryDTO> getList() {
        List<Category> entityList = categoryRepository.findAll();

        List<CategoryDTO> categoryList = entityList.stream()
                .map(entity -> entityToDto(entity))
                .collect(Collectors.toList());

        return categoryList;
    }
  
    
    // 카테고리 한개 조회
    @Override
    public CategoryDTO read(Integer categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        return category.isPresent() ? entityToDto(category.get()): null;
    }

    /*
     * 카테고리 저장
     *  - 화면에서 전달받은 정보를 저장하고 있는 CategoryDTO를 Category Entity로
     *    변환해서 영속 영역에 저장한다.
     */
    @Override
    public Category register(CategoryDTO dto) {
    	Category entity = dtoToEntity(dto);
        return categoryRepository.save(entity);
    }

    @Override
    public void modify(CategoryDTO categoryDTO) {
    	// 1. 수정할 카테고리 엔티티 조회
    	// Optional<Category> 감싸는 이유 : 결과가 nul 일수도 있기 때문
        Optional<Category> category = categoryRepository.findById(categoryDTO.getCategoryId());
        
        // 2. 엔티티가 존재할 경우 수정작업
        if (category.isPresent()) {
        	Category existingCategory = category.get();
            existingCategory.setCategoryName(categoryDTO.getCategoryName()); 	// 화면의 입력값으로 변경
            existingCategory.setDescription(categoryDTO.getDescription()); 		// 화면의 입력값으로 변경
            categoryRepository.save(existingCategory);	// 영속화
        } 
    }

    @Override
    public boolean remove(Integer categoryId) {
    	// 1. 삭제할 카테고리 엔티티 조회
        Optional<Category> user = categoryRepository.findById(categoryId);
        // 2. 존재할 경우 삭제 처리
        if (user.isPresent()) {
            categoryRepository.deleteById(categoryId);
            return true;
        } else {
            return false;
        }
    }
    
}
