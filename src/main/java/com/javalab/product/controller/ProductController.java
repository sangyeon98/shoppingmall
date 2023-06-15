package com.javalab.product.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.javalab.product.dto.CategoryDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.dto.ProductDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.entity.Product;
import com.javalab.product.service.CategoryService;
import com.javalab.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/product")
@Slf4j
public class ProductController {
	// 상품 서비스 의존성 주입[생성자 주입]
	// 두 개의 서비스 인터페이스가 주입됨.
    private final ProductService productService;
    private final CategoryService categoryService;
    public ProductController(ProductService productService,
    						CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // 상품 목록 조회
    @GetMapping("/list")
    public void getList(PageRequestDTO pageRequestDTO,
						Model model) {
    	PageResultDTO<ProductDTO, Product> result = productService.getList(pageRequestDTO);
    	model.addAttribute("result", result);
    }

    // 상품 한개 조회
    @GetMapping("/read")
    public void getProductById(@RequestParam Integer productId, Model model) {
    	log.info("getCategoryById");
    	ProductDTO dto = productService.read(productId);
        model.addAttribute("product", dto);
    }

    @GetMapping("/register")
    public void registerForm(@ModelAttribute("productDTO") ProductDTO productDTO,
				    		BindingResult bindingResult,
				    		PageRequestDTO pageRequestDTO,
				    		@ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
				    		Model model) {
    	
    	// 빈 객체 생성해서 전달(빈값 바인딩용)
        model.addAttribute("product", new Product());
        
        // 카테고리 목록을 드롭다운리스트로 출력하기 위한 데이터
        List<CategoryDTO> categoryList = categoryService.getList();
        model.addAttribute("categoryList", categoryList);
        
        log.info("categoryList.getSize() : " + categoryList.size());
    }
    
    /*
     * 상품 등록처리(Post 요청)
     *  - Category category 파라미터 
     *    : 등록 정보를 모두 담아주는 커맨트 객체
     *    : 등록하다 오류나면 입력정보를 그대로 갖고 다시 등록폼으로 가서
     *      입력했던 정보 그대로 세팅할 때 커맨드 객체가 필요함. 
     * @Valid : 뒤에 오는 커맨드 객체에 설정된 조건에 합당한지 검증
     *  - 검증후 문제가 있으면 다시 입력폼으로 이동     
     * BindingResult 
	 *  - BindingResult 객체는 검증 결과에 대한 결과 정보들을 담고 있습니다
	 *  - 검증 객체 바로 뒤어 오도록 한다.
	 * RedirectAttributes
	 *  - VO 입력값 전송
	 *  - 오류값 객체 전송  
     */
    @PostMapping("/register")
    public String register(@ModelAttribute @Valid ProductDTO product, 
    						BindingResult bindingResult, 
    						Model model) {
    	
        log.info("register process product.toString() " + product.toString());

        // 검증시 오류 있으면
        if (bindingResult.hasErrors()) {
        	
            // Log field errors
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error( error.getField() + " "+ error.getDefaultMessage());
            }
            
            model.addAttribute("product", product);
            
            // 카테고리 목록을 드롭다운리스트로 출력하기 위한 데이터
            List<CategoryDTO> categoryList = categoryService.getList();
            model.addAttribute("categoryList", categoryList);
            
            return "product/register";
        }
        
        // 검증 오류 없음
        productService.register(product);
        return "redirect:/product/list";
    }


    /*
     * 수정폼 띄워줌
     * - @PathVariable : url 주소에 함께 달려온 파라미터 추출
     */
    @GetMapping("/modify")
    public void modify(@RequestParam("productId") Integer productId, 
    		@ModelAttribute("productDTO") ProductDTO productDTO,
    		BindingResult bindingResult,
    		Model model) {
    	
    	log.info("modify - get");
    	
    	// 상품 조회
    	ProductDTO dto = productService.read(productId);
            	
        // 카테고리 목록을 드롭다운리스트로 출력하기 위한 데이터
        List<CategoryDTO> categoryList = categoryService.getList();
        model.addAttribute("categoryList", categoryList);
    	
        model.addAttribute("product", dto);
    }
    
    /*
    @GetMapping("/modify")
    public String modify(@RequestParam("productId") Integer productId, Model model) {
        log.info("modify - get");

        // 상품 조회
        ProductDTO dto = productService.read(productId);

        // 카테고리 목록을 드롭다운리스트로 출력하기 위한 데이터
        List<CategoryDTO> categoryList = categoryService.getList();
        
        // Set the isSelected property for the original category
        for (CategoryDTO category : categoryList) {
            if (category.getCategoryId().equals(dto.getCategoryId())) {
                category.setSelected(true);
                break;
            }
        }

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("product", dto);

        return "product/modify";
    }
    */
    
    
    

    /*
     *  상품 수정 처리[값 검증]
     *   - @ModelAttribute @Valid ProductDTO dto : 화면의 입력데이터 저장(커맨드 객체)
     *    : 화면의 값을 커맨드 객체의 속성으로 바인딩
     *    : 입력값 문제 있을 때 다시 수정폼으로 이동해서 해당값을 그대로 세팅할 때 사용
     *   - @Valid : 커맨드 객체의 값이 설정한 조건에 맞는지 검증
     *   - BindingResult bindingResult : 검증한 결과 저장 객체
     *    : 안에는 ProductDTO 객체를 통해서 오류 값이 보관되어 있어서 다시 돌아간
     *      타임리프 페이지에서 오류 메시지 사용함.
     */
    @PostMapping("/modify")
    public String modify(@ModelAttribute @Valid ProductDTO dto,
                         BindingResult bindingResult,
                         Model model) {

        log.info("modify - post dto: " + dto.toString());

        // Validation errors exist
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error(error.getField() + " " + error.getDefaultMessage());
            }

            // Get the original product data to populate the form
            ProductDTO originalProduct = productService.read(dto.getProductId());

            model.addAttribute("product", dto);

            // Category list for dropdown
            List<CategoryDTO> categoryList = categoryService.getList();
            model.addAttribute("categoryList", categoryList);

            return "product/modify";
        }

        productService.modify(dto);

        return "redirect:/product/list";
    }


    // 상품 삭제[미구현]
    @GetMapping("/delete/{productId}")
    public String deleteCategory(@PathVariable Integer productId) {
    	
        boolean deleted = productService.remove(productId);
        
        return "redirect:/category/list";
    }
}