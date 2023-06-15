package com.javalab.product.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.javalab.product.dto.CategoryDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Category;
import com.javalab.product.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

/**
 * [카테고리 컨트롤러]
 *  - CategoryDTO 밸리데이션 객체를 통해서 값을 검증함.
 *
 */
@Controller
@RequestMapping("/category")
@Slf4j
public class CategoryController {

	// 카테고리 서비스 의존성 주입[생성자 주입]
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 카테고리 목록 조회
    @GetMapping("/list")
    public void getList(PageRequestDTO pageRequestDTO,
						Model model) {
    	PageResultDTO<CategoryDTO, Category> result = categoryService.getList(pageRequestDTO);
        model.addAttribute("result", result);
    }

    // 카테고리 한 개 조회
    @GetMapping("/read")
    public void getCategoryById(@RequestParam Integer categoryId, Model model) {
    	
    	log.info("getCategoryById");
    	CategoryDTO dto = categoryService.read(categoryId);
    	
        model.addAttribute("category", dto);
        //return "categoryView";	// 카테고리 상세페이지(타임리프)
    }

    // 카테고리 등록폼(Get 요청)
//    @GetMapping("/register")
//    public void registerForm(Model model) {
//    	log.info("createForm");
//        model.addAttribute("category", new Category());
//        //return "categoryForm";	// 카테고리 폼
//    }

    @GetMapping("/register")
    public void registerForm(Model model, 
    		@ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
    		BindingResult bindingResult
    		//RedirectAttributes ra
    		) {
        log.info("createForm");
        model.addAttribute("category", new Category());

    }
    
    /*
     * 카테고리 등록처리(Post 요청)
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
    public String register(@ModelAttribute @Valid CategoryDTO category, 
    						BindingResult bindingResult, 
    						//RedirectAttributes ra,
    						Model model) {
        log.info("register");

        // 검증시 오류 있으면
        if (bindingResult.hasErrors()) {
        	
            // Log field errors
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error( error.getField() + " "+ error.getDefaultMessage());
            }

            /*
             * 오류값 객체 전송
             * "org.springframework.validation.BindingResult.categoryDTO"
             *  - 페이지에서 th:errors="${categoryDTO.categoryName}" 형태로 값을
             *    꺼내서 사용할 수 있음. 
             */
			//ra.addFlashAttribute("org.springframework.validation.BindingResult.categoryDTO", bindingResult);
            
            model.addAttribute("category", category);
            log.info("register");
            
            return "category/register";
        }
        
        // 검증 오류 없음
        categoryService.register(category);
        
        return "redirect:/category/list";
    }


    /*
     * 수정폼 띄워줌
     * - @PathVariable : url 주소에 함께 달려온 파라미터 추출
     */
    @GetMapping("/modify")
    public void modify(@RequestParam("categoryId") Integer categoryId, 
    		@ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
    		BindingResult bindingResult,
    		Model model) {
    	
    	log.info("modify - get");
    	
    	// 1. 카테고리 조회
    	CategoryDTO dto = categoryService.read(categoryId);
        
        model.addAttribute("category", dto);
    }

    /*
     *  카테고리 수정 처리[값 검증]
     *   - @ModelAttribute @Valid CategoryDTO dto : 화면의 입력데이터 저장(커맨드 객체)
     *    : 화면의 값을 커맨드 객체의 속성으로 바인딩
     *    : 입력값 문제 있을 때 다시 수정폼으로 이동해서 해당값을 그대로 세팅할 때 사용
     *   - @Valid : 커맨드 객체의 값이 설정한 조건에 맞는지 검증
     *   - BindingResult bindingResult : 검증한 결과 저장 객체
     *    : 안에는 CategoryDTO 객체를 통해서 오류 값이 보관되어 있어서 다시 돌아간
     *      타임리프 페이지에서 오류 메시지 사용함.
     */
    @PostMapping("/modify")
    public String modify(@ModelAttribute @Valid CategoryDTO dto,
						BindingResult bindingResult,
						Model model) {
    	
    	log.info("modify - post dto : " + dto.toString());
    	
    	 // 검증시 오류 있으면
        if (bindingResult.hasErrors()) {        	
            // Log field errors
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error( error.getField() + " "+ error.getDefaultMessage());
            }

            model.addAttribute("category", dto);
            return "category/modify";
        }
    	
        categoryService.modify(dto);

        return "redirect:/category/list";
    }

    // 카테고리 삭제
    @GetMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable Integer categoryId) {
    	
        boolean deleted = categoryService.remove(categoryId);
        
        return "redirect:/category/list";
    }
}
