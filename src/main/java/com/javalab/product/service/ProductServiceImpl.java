package com.javalab.product.service;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.javalab.product.dto.ProductDTO;
import com.javalab.product.dto.CategoryDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.dto.ProductDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.entity.Product;
import com.javalab.product.entity.Product;
import com.javalab.product.repository.CategoryRepository;
import com.javalab.product.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	// 생성자 의존성주입
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductServiceImpl(ProductRepository productRepository,
    							CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // 상품 목록 조회
    @Override
    public PageResultDTO<ProductDTO, Product> getList(PageRequestDTO requestDTO) {

        Pageable pageable = requestDTO.getPageable(Sort.by("productId").descending());

        Page<Product> result = productRepository.findAll(pageable);
        
        Function<Product, ProductDTO> fn = (entity -> entityToDto(entity)); // java.util.Function
        return new PageResultDTO<>(result, fn );
    }    
    

    
    // 상품 한개 조회
    @Override
    public ProductDTO read(Integer productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() ? entityToDto(product.get()): null;
    }

    /*
     * 상품 저장
     *  - 화면에서 전달받은 정보를 저장하고 있는 ProductDTO를 Category Entity로
     *    변환해서 영속 영역에 저장한다.
     */
    @Override
    public Product register(ProductDTO dto) {
    	Product entity = dtoToEntity(dto);
        return productRepository.save(entity);
    }

    @Override
    public void modify(ProductDTO productDTO) {
        Optional<Product> product = productRepository.findById(productDTO.getProductId());
        if (product.isPresent()) {
            Product existingProduct = product.get();
            existingProduct.setProductName(productDTO.getProductName());
            existingProduct.setPrice(productDTO.getPrice());
            
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("카테고리가 존재하지 않습니다."));
            
            existingProduct.setCategory(category); 
            existingProduct.setDescription(productDTO.getDescription());
            productRepository.save(existingProduct);
        }
    }

    
    
    @Override
    public boolean remove(Integer productId) {
    	// 1. 삭제할 상품 엔티티 조회
        Optional<Product> user = productRepository.findById(productId);
        // 2. 존재할 경우 삭제 처리
        if (user.isPresent()) {
            productRepository.deleteById(productId);
            return true;
        } else {
            return false;
        }
    }

    // 상품 전체 리스트 조회
    @Override
    public List<ProductDTO> getList() {
        List<Product> entityList = productRepository.findAll();

        List<ProductDTO> productList = entityList.stream()
                .map(entity -> entityToDto(entity))
                .collect(Collectors.toList());

        return productList;
    }
    
}
