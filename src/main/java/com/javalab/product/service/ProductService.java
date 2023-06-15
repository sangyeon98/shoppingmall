package com.javalab.product.service;

import com.javalab.product.dto.ProductDTO;

import java.util.List;

import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.entity.Product;

public interface ProductService {

	public PageResultDTO<ProductDTO, Product> getList(PageRequestDTO requestDTO);    
    ProductDTO read(Integer ProductId);    
    Product register(ProductDTO Product);			
    void modify(ProductDTO ProductDTO);
	boolean remove(Integer ProductId);    
    
	/*
	 *  DTO --> Entity 전환을 위한 default 메소드
	 *  default 메소드는 기존의 인터페이스를 구현해서 사용하는 Impl클래스들이
	 *  의무적으로 구현하지 않아도 오류가 발생하지 않는다.
	 *  
	 *  Dto를 파라미터로 받아서 Entity에 담아준다. 왜냐하면 화면에서 받아서
	 *  저장할 때는 Entity 형태로 save(entity)해야 하기 때문이다. 물론
	 *  안해도 되지만 일반적으로 영속 영역에는 Entity만 넣는게 좋다.
	 */
	default Product dtoToEntity(ProductDTO dto) {
		
		// 화면에서 전달된 categoryId로 카테고리 객체 생성
		Category category = Category.builder().categoryId(dto.getCategoryId()).build();
		
		Product entity = Product.builder()
							.productName(dto.getProductName())
							.price(dto.getPrice())
							.category(category)
							.description(dto.getDescription())
							.build();
		return entity;
	}

	/*
	 *  DTO --> Entity 전환을 위한 default 메소드
	 *  default 메소드는 기존의 인터페이스를 구현해서 사용하는 Impl클래스들이
	 *  의무적으로 구현하지 않아도 오류가 발생하지 않는다.
	 */

	default ProductDTO entityToDto(Product entity) {

		ProductDTO dto = ProductDTO.builder()
							.productId(entity.getProductId())
							.productName(entity.getProductName())
							.price(entity.getPrice())
							.categoryId(entity.getCategory().getCategoryId())
							.description(entity.getDescription())
							.regDate(entity.getRegDate())
							.build();

		return dto;
	}
	public List<ProductDTO> getList();
	

}
