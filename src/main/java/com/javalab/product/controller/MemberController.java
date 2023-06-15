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

import com.javalab.product.dto.MemberDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;
import com.javalab.product.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/list")
    public void getList(PageRequestDTO pageRequestDTO, Model model) {
        PageResultDTO<MemberDTO, Member> result = memberService.getList(pageRequestDTO);
        model.addAttribute("result", result);
    }

    @GetMapping("/read")
    public void getMemberByEmail(@RequestParam String email, Model model) {
        log.info("getMemberByEmail");
        MemberDTO dto = memberService.read(email);
        model.addAttribute("member", dto);
    }

    @GetMapping("/register")
    public void registerForm(@ModelAttribute("memberDTO") MemberDTO memberDTO,
                             BindingResult bindingResult,
                             PageRequestDTO pageRequestDTO,
                             Model model) {
        model.addAttribute("member", new Member());
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid MemberDTO member,
                           BindingResult bindingResult,
                           Model model) {
        log.info("register process member.toString() " + member.toString());

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error(error.getField() + " " + error.getDefaultMessage());
            }

            model.addAttribute("member", member);
            return "member/register";
        }

        memberService.register(member);
        return "redirect:/member/list";
    }

    @GetMapping("/modify")
    public void modify(@RequestParam("email") String email,
                       @ModelAttribute("memberDTO") MemberDTO memberDTO,
                       BindingResult bindingResult,
                       Model model) {
        log.info("modify - get");

        MemberDTO dto = memberService.read(email);
        log.info("여기는 dto를 확인해보자 : " +dto);
        model.addAttribute("member", dto);
    }

    @PostMapping("/modify")
    public String modify(@ModelAttribute @Valid MemberDTO memberDTO,
                         BindingResult bindingResult,
                         Model model) {
        log.info("modify - post dto: " + memberDTO.toString());

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError error : fieldErrors) {
                log.error(error.getField() + " " + error.getDefaultMessage());
            }

            model.addAttribute("member", memberDTO);
            return "member/modify";
        }

        memberService.modify(memberDTO);

        return "redirect:/member/list";
    }

    @GetMapping("/delete/{email}")
    public String deleteMember(@PathVariable String email) {
        boolean deleted = memberService.remove(email);
        return "redirect:/member/list";
    }
}
