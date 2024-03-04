package com.example.dataDelivery.controller;//package com.example.jpaPlease.controller;

import com.example.dataDelivery.entity.Comment;
import com.example.dataDelivery.entity.Content;
import com.example.dataDelivery.repository.MyRepository;
import com.example.dataDelivery.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class MyController {
    private final MyRepository contentRepository;
    private final MyRepository memberRepository;
    private final MyRepository commentRepository;
    private Member logined = null;

    @Autowired
    public MyController(MyRepository contentRepository, MyRepository memberRepository, MyRepository commentRepository) {
        this.contentRepository = contentRepository;
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
    }


    @GetMapping("/home")
    public String home(){
        if(logined != null) {
            return "form";
        }
        else return "redirect:/login";
    }

    @PostMapping("/received")
    public String save(Content content, Model model) {
        if(logined != null) {
            contentRepository.save(content);
            model.addAttribute("content", content);
            return "redirect:/list";
        } else{
            return "redirect:/login";
        }
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


    // 게시글 상세 페이지
    @GetMapping("/details/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        if(logined != null) {
            Optional<Content> findContent = contentRepository.findById(id);
            Optional<ArrayList<Comment>> findComments = commentRepository.findByContentId(id);
            if (findContent.isPresent()) {
                model.addAttribute("content", findContent.get());
                model.addAttribute("comments", findComments.get()); //이거 어떻게????
                return "detail";
            } else {
                // Handle the case where the content is not found
                // You can add an attribute to show an error message or redirect to a custom error page
                return "redirect:/content-not-found";
            }
        }
        else {
            return "redirect:/login";
        }
    }


    //댓글 작성
    @PostMapping("/comments/add")
    public String addComments(@ModelAttribute("newComment") Comment newComment,
                              @RequestParam("contentId") Long contentId) {
        if(logined != null) {
            newComment.setContentId(contentId);
            newComment.setWriter(logined.getName());
            commentRepository.save(newComment);
            return "redirect:/details/" + contentId;
        } else {
            return "redirect:/login";
        }
    }


    //댓글 삭제
    @PostMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable("id") Long commentId,
                                @RequestParam("contentId") Long contentId) {
        if(logined != null) {
            commentRepository.deleteById(commentId);
            return "redirect:/details/" + contentId;
        } else{
            return "redirect:/login";
        }
    }


    //댓글 수정
    @GetMapping("/comments/edit/{id}")
    public String editCommentForm(@PathVariable("id") Long commentId, Model model) {
        if(logined != null) {
            Optional<Comment> findComment = commentRepository.findById(commentId);
            if (findComment.isPresent()) {
                model.addAttribute("comment", findComment.get());
                return "edit_comment"; // 댓글 수정 폼의 뷰 이름
            } else {
                return "/home"; // 콘텐츠를 찾을 수 없을 때 리다이렉션
            }
        } else{
            return "redirect:/login";
        }
    }



    @PostMapping("/comments/edit/{contentId}")
    public String editComment(@PathVariable("contentId") Long contentId,
                              @RequestParam("id") Long commentId,
                              @RequestParam("text") String text) {
        if(logined != null) {
            // 댓글 수정 폼으로 이동하는 로직 구현
            Optional<Comment> findComment = commentRepository.findById(commentId);
            if (findComment.isPresent()) {
                Comment comment = findComment.get();
                comment.setText(text);
                commentRepository.update(comment);
                return "redirect:/details/" + contentId;
            } else {
                return "redirect:/content-not-found";
            }
        } else{
            return "redirect:/login";
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
        memberRepository.save(member);
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("id") String id,
                        @RequestParam("password") String password, Model model) {
            Optional<Member> target = memberRepository.findById(id);
            if (target.isPresent() && target.get().getPassword().equals(password)) {
                logined = target.get();
                return "redirect:/home";
            } else {
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
