[프로젝트 특이사항]

 1. JPA Entity
 
 
 2. Builder
 
 
 3. Spring Bean Validation
 
 
 4. 컨트롤러에서 메소드 반환타입을 void로 설정하는 이유
 
 
 4. application.properties Thymeleaf 모든 설정 삭제
  - 프리마커, 머스테치 미사용하므로 별도 설정 필요 없음.
  - Thymeleaf 페이지 찾아가는 매커니즘 설명.
  
 5. DTO <--> Entity
  - Service Layer의 인터페이스에 default 메소드에서 전환작업 진행
  - ServiceImpl 에서 default 메소드 사용함. 
 
 6. service interface default method
 
 /**
 [Detail]
 
  [상품관리 시스템]
 
 chap63_jpa_product
 
1. 수업게시판 : 283번 게시물 [1차 완성] 상품관리
 
2. Category.java 엔티티 regDate 수정
 
 	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime regDate = LocalDateTime.now(); // 현재일자

3. 단위테스트, 더미(테스트용 샘플데이터) 데이터 생성

package com.javalab.product.repository;

/**
 * 카테고리 엔티티 관련 Repository 단위 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase
	(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

	/*
	 * 카테고리 더미 데이터 저장
	 */
	//@Test
	//@Disabled
	@Commit	// 기본은 롤백
	public void testCategoryRegister() {
		IntStream.rangeClosed(1, 100).forEach(i -> {
			Category category = new Category();
			category.setCategoryName("Category" + i);
			category.setDescription("Category 설명" + i);
			
			// 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
			categoryRepository.save(category);	
		});
		log.info("Category 저장 완료!");	
	}

	/*
	 * 카테고리 더미 데이터 저장
	 */
	@Test
	//@Disabled
	@Commit	// 기본은 롤백
	public void testCategoryRegisterBuilder() {
		IntStream.rangeClosed(101, 200).forEach(i -> {
			Category category =	Category.builder()
					.categoryName("Category Builder" + i)
					.description("Category  Builder 설명" + i)
					.build();
					
			// 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
			categoryRepository.save(category);	
		});
		log.info("Category 저장 완료!");	
	}


	
	/*
	 * 카테고리 목록 조회(페이지네이션/정렬 기능 구현)
	 *  Spring Data Jpa의 페이징, 정렬은 findAll(pageable)메소드 사용
	 *   - findAll(Pageable) : PagingAndSortRepository 소속 메소드
	 *   - Page<T> : 반환타입이 Page<T> 타입인 경우 에는 인자가 반드시 Pageable이어야 함.
	 *   - Pageable : 페이징 관련 정보를 담고 있는 객체가 구현해야 할 인터페이스.
	 *     어떻게 페이징과 정렬을 해야 하는지에 대한 정보가 있다.
	 *   - PageRequest : Pageable 인터페이스를 구현한 구현체(요청페이지, 게시물수, 정렬) 
	 *   - static of() : PageRequest 객체를 얻기 위해서 필요한 정적 메소드
	 *   - Sort.by("id") : 정렬 컬럼(필드)
	 */
	@Test
	//@Disabled
	public void testCategoryPagingSorting() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("categoryId").descending());
		Page<Category> result = categoryRepository.findAll(pageable);
		result.stream().forEach(c -> log.info(c.toString()));
	}
	
	/* 
	 *  카테고리 한개 조회
	 */
	//@Test
	//@Disabled
	public void testGetCategoryById() {
		Optional<Category> optCategory = 
			categoryRepository.findById(1); // 데이터베이스 확인
		Category category = optCategory.get();
		log.info("카테고리 한개 조회 완료 : " + category.toString());
	}		
	

	/* 
	 *  카테고리 수정
	 */
	//@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testCategoryUpdate() {
		
		Category category = new Category();
		category.setCategoryId(1); // 수정할 카테고리 ID 지정
		category.setCategoryName("Category" + "변경됨");
		category.setDescription("변경된 Category");
		// 영속화 - 엔티티 매니저가 관리하는 메모리로 저장됨
		categoryRepository.save(category);
		
		log.info("수정 완료!");	
	}
	
	/* 
	 *  카테고리 삭제
	 */
	@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testCategoryDelete() {
		
		categoryRepository.deleteById(10); // 실제로 있는 카테고리 ID
		
		log.info("삭제 완료!");	
	}		
	
}

4. Product 단위테스트
/**
 * Product 엔티티 관련 Repository 단위 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // 카테고리 레파지토리가 필요해서 의존성을 주입함.
    // 상품 저장시 실제로 있는 카테고리를 넣기 위해서 조회할 때 필요함.
    @Autowired
    private CategoryRepository categoryRepository;
    
	/*
	 * Product 더미 데이터 저장
	 *  - Validation 조건을 체크하고 그에 맞게 샘플 데이터를 입력할것.
	 *    조건에 맞지 않으면 오류나며 원인을 찾기 힘듦.
	 */
	//@Test
	//@Disabled
	@Commit	// 기본은 롤백
	public void testProductSave() {
		
		// 상품 등록시 사용할 실제로 존재하는 카테고리 객체 조회
		Integer categoryId = 1;
	    Category category = 
		categoryRepository.findById(categoryId)
			.orElseThrow(); // Retrieve an existing Category entity from the database

		IntStream.rangeClosed(101, 200).forEach(i -> {
			Product product = new Product();
			product.setProductName("Product Name" + i);
			product.setPrice(120000);
			product.setCategory(category);
			product.setDescription("상품 설명" + i);
			product.setRegDate(LocalDateTime.now());
			
			// 영속화(메모리의 엔티티 매니저의 관리 대상이 됨)
			productRepository.save(product);	
		});
		
		log.info("Product 저장 완료!");	
	}
	
	/*
	 * Product 목록 조회(페이지네이션/정렬 기능 구현)
	 *  Spring Data Jpa의 페이징, 정렬은 findAll(pageable)메소드 사용
	 *   - findAll(Pageable) : PagingAndSortRepository 소속 메소드
	 *   - Page<T> : 반환타입이 Page<T> 타입인 경우 에는 인자가 반드시 Pageable이어야 함.
	 *   - Pageable : 페이징 관련 정보를 담고 있는 객체가 구현해야 할 인터페이스.
	 *     어떻게 페이징과 정렬을 해야 하는지에 대한 정보가 있다.
	 *   - PageRequest : Pageable 인터페이스를 구현한 구현체(요청페이지, 게시물수, 정렬) 
	 *   - static of() : PageRequest 객체를 얻기 위해서 필요한 정적 메소드
	 *   - Sort.by("id") : 정렬 컬럼(필드)
	 */
	//@Test
	//@Disabled
	public void testProductPagingSorting() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("productId").descending());
		Page<Product> result = productRepository.findAll(pageable);
		result.stream().forEach(c -> log.info(c.toString()));
	}
	
	/* 
	 *  Product 한개 조회
	 */
	//@Test
	//@Disabled
	public void testGetProductById() {
		Optional<Product> optProduct = 
			productRepository.findById(300); // 데이터베이스 확인
		Product product = optProduct.get();
		log.info("Product 한개 조회 완료 : " + product.toString());
	}		

	/* 
	 *  Product 수정
	 */
	//@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testProductUpdate() {
		
		// 상품 등록시 사용할 실제로 존재하는 카테고리 객체 조회
		Integer categoryId = 9;
	    Category category = 
		categoryRepository.findById(categoryId)
			.orElseThrow(); // 실제로 존재하는 카테고리
		
	    // 업데이트 할 때는 Validation조건에 맞도록 할것.
		Product product = new Product();
		product.setProductId(11);	// 실존하는 상품Id으로
		product.setProductName("변경된 상품명입니다."); // 반드시 8~50자 사이로 입력
		product.setPrice(325000);
		product.setDescription("변경된 상품 설명입니다.");
		product.setCategory(category);	// 실존하는 카테고리 객체
		product.setRegDate(LocalDateTime.now());
		
		// 영속화 - 엔티티 매니저가 관리하는 메모리로 저장됨
		productRepository.save(product);
		
		log.info("수정 완료!");	
	}
	
	/* 
	 *  Product 삭제
	 */
	@Test
	//@Disabled
	@Commit	// 기본 롤백
	public void testProductDelete() {
		
		productRepository.deleteById(10); // 실제로 있는 Product ID
		
		log.info("삭제 완료!");	
	}		
	
}


[Spring Bean Validation Test]
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
     *  - @NotNull 값만 들어오면 통과
     *  - @NotEmpty : " "면 통과
     *  - @NotBlank : null, "", " " 모두 불가 
     */
	@Test
    public void testValidNotNullEmptyBlank() {
    	CategoryDTO categoryDTO = CategoryDTO.builder()
    			.categoryName(" ")
    			.description(" ")
    			.build();    	
    	assertTrue(validator.validate(categoryDTO).isEmpty());
    }

[CategoryController] 검증 역할.

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
    @GetMapping("/list") // "/category/list"
    public void getList(PageRequestDTO pageRequestDTO,
						Model model) {
    	
    	PageResultDTO<CategoryDTO, Category> result = categoryService.getList(pageRequestDTO);
    	
    	// 디버깅
    	//for (CategoryDTO dto : result.getDtoList()) {
		//	log.info(dto.toString());
		//}
    	
        model.addAttribute("result", result);
        //return "categoryList";	// 타임리프 페이지
    }

    // 카테고리 한 개 조회
    @GetMapping("/read")	// /category/read
    public void getCategoryById(@RequestParam Integer categoryId, Model model) {
    	
    	log.info("getCategoryById");
    	CategoryDTO dto = categoryService.read(categoryId);
    	
        model.addAttribute("category", dto);
		
        //return "category/read";	// 카테고리 상세페이지(타임리프)
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
	BindingResult bindingResult) {
        log.info("createForm");
        model.addAttribute("category", new Category());

        // categoryDTO 객체가 없을 경우 만들어서 전달
        if (!model.containsAttribute("categoryDTO")) {
            model.addAttribute("categoryDTO", categoryDTO);
        }

        // 오류 검증 결과를 model에 담음
        model.addAttribute("org.springframework.validation.BindingResult.categoryDTO", bindingResult);
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
	 * RedirectAttributes
	 *  - VO 입력값 전송
	 *  - 오류값 객체 전송  
     */
    @PostMapping("/register")
    public String register(@ModelAttribute @Valid CategoryDTO category, 
    						BindingResult bindingResult, 
    						RedirectAttributes ra,
    						Model model) {
        log.info("register");

        // 검증시 오류 있으면
        if (bindingResult.hasErrors()) {
        	
            // Log field errors
            List<FieldError> fieldErrors = 
					bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error( error.getField() + " "+ error.getDefaultMessage());
            }

            /*
             * 오류값 객체 전송
             * "org.springframework.validation.BindingResult.categoryDTO"
             *  - 페이지에서 th:errors="*{categoryDTO.categoryName}" 형태로 값을
             *    꺼내서 사용할 수 있음. 
             */
			ra.addFlashAttribute("org.springframework.validation.BindingResult.categoryDTO", 
					bindingResult);
            
            model.addAttribute("category", category);
            log.info("register");
            
            return "category/register";
        }
        
        // 검증 오류 없음
        categoryService.register(category);
        
        return "redirect:category/list";
    }


    /*
     * 수정폼 띄워줌
     * - @PathVariable : url 주소에 함께 달려온 파라미터 추출
     */
    @GetMapping("/modify")	// /category/modify
    public void modify(@PathVariable Integer categoryId, Model model) {
    	
    	log.info("categoryModifyForm");
    	
    	// 1. 카테고리 조회
    	CategoryDTO dto = categoryService.read(categoryId);
        
        model.addAttribute("category", dto);
        //return "categoryModifyForm";
    }

    // 카테고리 수정 처리
    @PostMapping("/modify")
    public String modify(CategoryDTO dto,
					     @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
					     RedirectAttributes redirectAttributes) {
    	log.info("modify");
    	
        categoryService.modify(dto);

        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());

        redirectAttributes.addAttribute("categoryId",dto.getCategoryId());

        return "redirect:category/list";    	
    	
    }

    // 카테고리 삭제
    @PostMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable Integer categoryId) {
        boolean deleted = categoryService.remove(categoryId);
        return "redirect:category/list";
    }
}


[application.properties]

server.port:8080

#spring.thymeleaf.enabled=true
spring.thymeleaf.cache=false
#spring.thymeleaf.check-template-location=true
#spring.thymeleaf.prefix=classpath:/templates/
#spring.thymeleaf.suffix=.html

server.servlet.context-path=/

spring.devtools.livereload.enabled=true
spring.devtools.restart.enabled=true

# character encoding
server.servlet.encoding.charset=utf-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

# MariaDB setting(SQL Log)
spring.datasource.driverClassName=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
spring.datasource.url=jdbc:log4jdbc:mariadb://localhost:3306/product?serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=1234

# From Spring Boot 2 version, HikariCP is used by default.
# Set HikariCP Maximum Pool Size to 4
spring.datasource.hikari.maximum-pool-size=4 

# JPA
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=true


# 파일 업로드시 필요한 설정
spring.servlet.multipart.enabled=true
spring.servlet.multipart.location=C:\\filetest
spring.servlet.multipart.max-request-size=30MB
spring.servlet.multipart.max-file-size=10MB

logging.level.root=debug
 
 **/
 
 
 [실행시키면 다음과 같은 쿼리문이 생성됨]
 
 create table tbl_product (
       product_id integer not null auto_increment,
        description varchar(100),
        price integer(10) default 0,
        product_name varchar(50) not null,
        reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        category_id integer,
        primary key (product_id)
    ) engine=InnoDB
2023-06-13 21:04:10.392 [restartedMain] DEBUG jdbc.sqlonly ---  com.zaxxer.hikari.pool.ProxyStatement.execute(ProxyStatement.java:94)
1. create table tbl_product (
       product_id integer not null auto_increment,
        description varchar(100),
        price integer(10) default 0,
        product_name varchar(50) not null,
        reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        category_id integer,
        primary key (product_id)
    ) engine=InnoDB

2023-06-13 21:04:10.392 [restartedMain] DEBUG o.m.jdbc.client.impl.StandardClient --- execute query: 
    create table tbl_product (
       product_id integer not null auto_increment,
        description varchar(100),
        price integer(10) default 0,
        product_name varchar(50) not null,
        reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        category_id integer,
        primary key (product_id)
    ) engine=InnoDB
21:04:10,422 |-INFO in c.q.l.core.rolling.helper.TimeBasedArchiveRemover - Removed  0 Bytes of files
2023-06-13 21:04:10.740 [restartedMain] DEBUG jdbc.sqltiming ---  com.zaxxer.hikari.pool.ProxyStatement.execute(ProxyStatement.java:94)
1. create table tbl_product (
       product_id integer not null auto_increment,
        description varchar(100),
        price integer(10) default 0,
        product_name varchar(50) not null,
        reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        category_id integer,
        primary key (product_id)
    ) engine=InnoDB
 {executed in 347 msec}
Hibernate: 
    
    alter table tbl_product 
       add constraint FKfq7110lh85cseoy13cgni7pet 
       foreign key (category_id) 
       references tbl_category (category_id)
2023-06-13 21:04:10.742 [restartedMain] DEBUG jdbc.sqlonly ---  com.zaxxer.hikari.pool.ProxyStatement.execute(ProxyStatement.java:94)
1. alter table tbl_product 
       add constraint FKfq7110lh85cseoy13cgni7pet 
       foreign key (category_id) 
       references tbl_category (category_id)

2023-06-13 21:04:10.742 [restartedMain] DEBUG o.m.jdbc.client.impl.StandardClient --- execute query: 
    alter table tbl_product 
       add constraint FKfq7110lh85cseoy13cgni7pet 
       foreign key (category_id) 
       references tbl_category (category_id)
2023-06-13 21:04:11.889 [restartedMain] DEBUG jdbc.sqltiming ---  com.zaxxer.hikari.pool.ProxyStatement.execute(ProxyStatement.java:94)
1. alter table tbl_product 
       add constraint FKfq7110lh85cseoy13cgni7pet 
       foreign key (category_id) 
       references tbl_category (category_id)
 
 
 [BaseEntity]
 
 1. 진입점 클래스에 다음 추가
 @EnableJpaAuditing
 
 2. 카테고리 화면에서 데이터입력하면 날짜는 안넘어가기 때문에 날짜가 널이 입력된다.
  1) 엔티티에 BaseEntity 추가
  2) Category, Product에 extends BaseEntity 추가 
  
 3. 카테고리/상품 엔티티에서 날짜 주석처리 
 
 	/* 
	 *  Product 한개 조회
	 *  
	 *  [Fetch 전략 - Eager / Lazy]
	 *   - Fetch 전략을 Lazy로 하게 되면 지연로딩이 되어 처음에는 상품 테이블만 조회하고
	 *     카테고리 테이블이 필요한 시점 즉, product.toString()이 되는 시점에
	 *     카테고리 테이블에 조회해서 카테고리 관련 모든 정보를 조회한다.
	 *   - Fetch 전략을 안주거나, Eager로 하게 되면 처음 부터 상품 테이블과
	 *     카테고리 테이블을 조인걸게 된다. 그렇게 되면 데이터베이스에 성능에
	 *     좋지 않다. 가급적이면 지연로딩을 사용하는게 낫다.
	 */
	//@Test
	//@Disabled
	public void testGetProductById() {
		Optional<Product> optProduct = productRepository.findById(1); // 데이터베이스 확인
		Product product = optProduct.get();
		log.info("Product의 이름 조회 : " + product.getProductName());
		// 1. 상품 엔티티의 Category 멤버 변수에 지연로딩을 설정해놓게 되면 
		//    이 시점까지는 상품 테이블만 조회함.
		// 2. 아래의 product.toString()으로 상품 정보의 모든 내용(Category포함)
		//    보겠다고 하는 시점에 그제서야 카테고리 테이블에 조회를 한다.
		log.info("Product 전체 정보 조회 : " + product.toString());
		
	}	
	
	
public interface ProductRepository extends JpaRepository<Product, Integer>{

	/*
	 * 연관관계가 있는 엔티티를 조인해서 동시에 상품정보와 카테고리 정보 추출
	 * 상품 테이블에는 카테고리ID만 있고 카테고리 이름이 없다. 그래서 조인해서
	 * 갖고 오려면 다음과 같이 JPQL 쿼리를 문을 작성해야 한다.
	 */
    @Query("select p, c from Product p left join p.category c where p.productId = :productId")
    Object getProductWithCategory(@Param("productId") Integer productId);
    
}	

Hibernate: 
    select
        product0_.product_id as product_1_1_0_,
        category1_.category_id as category1_0_1_,
        product0_.moddate as moddate2_1_0_,
        product0_.regdate as regdate3_1_0_,
        product0_.category_id as category7_1_0_,
        product0_.description as descript4_1_0_,
        product0_.price as price5_1_0_,
        product0_.product_name as product_6_1_0_,
        category1_.moddate as moddate2_0_1_,
        category1_.regdate as regdate3_0_1_,
        category1_.category_name as category4_0_1_,
        category1_.description as descript5_0_1_ 
    from
        tbl_product product0_ 
    left outer join
        tbl_category category1_ 
            on product0_.category_id=category1_.category_id 
    where
        product0_.product_id=?