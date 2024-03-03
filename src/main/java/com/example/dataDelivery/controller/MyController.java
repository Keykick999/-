package com.example.dataDelivery.controller;//package com.example.jpaPlease.controller;

import com.example.dataDelivery.entity.Content;
import com.example.dataDelivery.repository.MyRepository;
import com.example.dataDelivery.entity.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class MyController {
    private final MyRepository contentRepository;
    private final MyRepository memberRepository;
    private Member logined = null;

    @Autowired
    public MyController( MyRepository contentRepository,MyRepository memberRepository) {
        this.contentRepository = contentRepository;
        this.memberRepository = memberRepository;
    }


    @GetMapping("/home")
    public String home(){
        if(logined == null) {
            return "redirect:/login";
        }
        else return "form";
    }

    @PostMapping("/received")
    public String save(Content content, Model model) {
        contentRepository.save(content);
        model.addAttribute("content", content);
        return "redirect:/list";
    }


    // 전체 목록
    @GetMapping("/list")
    public String list(Model model) {
        if(logined != null) {
            List<Content> contents = contentRepository.findAll();
            model.addAttribute("contents", contents);
            return "index";
        }
        else {
            return "redirect:/login";
        }
    }


    // 단일 조회
    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        Optional<Content> target = contentRepository.findById(id);
        if (target.isPresent()) {
            model.addAttribute("content", target.get());
            return "detail";
        } else {
            // Handle the case where the content is not found
            // You can add an attribute to show an error message or redirect to a custom error page
            return "redirect:/content-not-found";
        }
    }


    @GetMapping("/check-id")
    public ResponseEntity<?> checkContentId(@RequestParam("id") Long id) {
        Optional<Content> existingContent = contentRepository.findById(id);
        if (existingContent.isPresent()) {
            return ResponseEntity.ok(Collections.singletonMap("exists",true));
        } else {
            return ResponseEntity.ok(Collections.singletonMap("exists",false));
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        if(logined != null) {
            Optional<Content> contentOptional = contentRepository.findById(id);
            if (contentOptional.isPresent()) {
                model.addAttribute("content", contentOptional.get());
                return "editContent";
            } else {
                return "redirect:/";
            }
        }
        else{
            return "redirect:/login";
        }
    }


    @PostMapping("/edit/{id}")
    public String updateContent(Content content){
        if(logined != null) {
            contentRepository.update(content);
            return "redirect:/list";
        }
        else{
            return "redirect:/login";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteContent(@PathVariable("id") Long id) {
        if(logined != null) {
            contentRepository.deleteById(id);
            return "redirect:/list";
        }
        else {
            return "redirect:/login";
        }
    }



    @GetMapping("/signup")
    public String signUp(Member member) {
        if(logined == null) {
            return "signup";
        }
        else {
            return "redirect:/home";
        }
    }

    @PostMapping("/signup")
    public String sign(Member member){
        if(member == null) System.out.println("null임");
        else {
            System.out.println(member);
        }
        memberRepository.save(member);
        return "redirect:/";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("id") String id,
                        @RequestParam("password") String password, Model model) {
        Optional<Member> target = memberRepository.findById(id);
        if(target.isPresent() && target.get().getPassword().equals(password)){
            logined = target.get();
            return "redirect:/home";
        }
        else {
            return "redirect:/login";
        }
    }



    @PostMapping("/logout")
    public String handleLogout() {
        logined = null;
        return "redirect:/login";
    }


    @GetMapping("/find-id")
    public String findId() {
        // 여기에 사용자의 이메일 주소를 확인하는 로직을 추가
        // 인증 문자 생성
//        String authText = "인증 문자입니다.";

        // 이메일로 인증 문자 보내기
//        emailService.sendSimpleMessage(email, "아이디 찾기 인증", authText);

        return "find-id";
    }

    @GetMapping("/find-password")
    public String findPassword() {
        return "find-password";
    }
}
