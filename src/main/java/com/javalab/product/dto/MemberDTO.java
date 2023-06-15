package com.javalab.product.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    @NotBlank(message = "이메일 필수 입력")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "패스워드 필수 입력")
    private String password;

    @NotBlank(message = "이름 필수 입력")
    @Size(min = 2, max = 50, message = "이름은 2자 ~ 50자 이내로 입력하세요.")
    private String name;

    @NotBlank(message = "주소 필수 입력")
    private String address;
    
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}