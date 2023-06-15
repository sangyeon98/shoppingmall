package com.javalab.product.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalab.product.dto.OrderMasterDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.dto.ProductDTO;
import com.javalab.product.entity.OrderMaster;
import com.javalab.product.service.OrderMasterService;
import com.javalab.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderMasterController {
	
	//@Autowired
	private final OrderMasterService orderMasterService;
	// 주문화면에서 상품 드롭다운 리스트 데이터 생성하기 위한 용도
	private final ProductService productService;
	
	//밑에 줄 없애고 @Autowired 써도 된다.
	public OrderMasterController(OrderMasterService orderMasterService,
								ProductService productService) {
		this.orderMasterService = orderMasterService;
		this.productService = productService;
	}

	@GetMapping("/list")
	public void getList(PageRequestDTO pageRequestDTO, Model model) {
		PageResultDTO<OrderMasterDTO, OrderMaster> result = orderMasterService.getList(pageRequestDTO);
		model.addAttribute("result", result);
	}

	@GetMapping("/read")
	public void getOrderMasterById(@RequestParam Integer orderId, Model model) {
		log.info("getOrderMasterById");
		OrderMasterDTO dto = orderMasterService.read(orderId);
		log.info("getOrderMasterById : " + dto.toString());
		model.addAttribute("orderMaster", dto);
	}

	@GetMapping("/register")
	public void registerForm(@ModelAttribute("orderMasterDTO") OrderMasterDTO orderMasterDTO,
							BindingResult bindingResult, 
							PageRequestDTO pageRequestDTO, 
							Model model) {
		
		model.addAttribute("orderMasterDTO", new OrderMasterDTO());
		
		// 상품 목록 조회해서 전달
        List<ProductDTO> productList = productService.getList();
        model.addAttribute("productList", productList);
	}
	
	// 상품추가 버튼 클릭시 추가된 상품 드롭다운 리스트에 채울 값을 생성하는 메소드
	@GetMapping("/product")
	@ResponseBody
	public List<ProductDTO> productListForDropdown() {
		
		// 상품 목록 조회해서 전달
		List<ProductDTO> productList = productService.getList();
		return productList;
	}

	// 저장 처리[일반적인 방식]
//	@PostMapping("/register")
//	public String registerOrder(@ModelAttribute("orderMasterDTO") OrderMasterDTO orderMasterDTO,
//								BindingResult bindingResult,
//								PageRequestDTO pageRequestDTO, 
//								Model model) {
//	    
//		log.info("orderMasterDTO.toString() : " + orderMasterDTO.toString());
//		
//		
//		if (bindingResult.hasErrors()) {
//			log.info("주문 정보에 오류가 있습니다.");
//			// 오류 처리 로직
//			// 다시 입력 페이지로 이동
//			return "/order/register";
//	    }
//	    
//		// 저장작업 OrderMaster and OrderItems
//	    orderMasterService.saveOrderMaster(orderMasterDTO); 
//	    
//	    // 목록으로 이동
//	    return "redirect:/order/list";
//	}
	
	/*
	 * 저장처리 메소드[Rest 방식]
	 * - @RequsetBody : 화면에서 ajax 방식의 JSON Type으로 넘어오는
	 *  데이터를 커맨드 객체에 자동으로 바인딩 해준다.
	 */
	@PostMapping("/register")
	@ResponseBody
							//json타입을 일반적으로 바꿔줌
	public String registerOrder(@RequestBody OrderMasterDTO orderMasterDTO) {

		log.info("저장 처리 시작 : " + orderMasterDTO.toString());
	    
	    orderMasterService.register(orderMasterDTO);
	        
	    return "저장 작업 성공";
	}
	
	@GetMapping("/modify")
	public void modify(@RequestParam("orderId") Integer orderId,
			@ModelAttribute("orderMasterDTO") OrderMasterDTO orderMasterDTO, BindingResult bindingResult, Model model) {
		log.info("modify - get");

		OrderMasterDTO dto = orderMasterService.read(orderId);
		model.addAttribute("orderMaster", dto);
	}

	@PostMapping("/modify")
	public String modify(@ModelAttribute @Valid OrderMasterDTO orderMasterDTO, BindingResult bindingResult,
			Model model) {
		log.info("modify - post dto: " + orderMasterDTO.toString());

		if (bindingResult.hasErrors()) {
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			for (FieldError error : fieldErrors) {
				log.error(error.getField() + " " + error.getDefaultMessage());
			}

			model.addAttribute("orderMaster", orderMasterDTO);
			return "order/modify";
		}

		orderMasterService.modify(orderMasterDTO);

		return "redirect:/order/list";
	}

}
