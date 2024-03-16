package com.example.dataDelivery.controller;//package com.example.jpaPlease.controller;

import com.example.dataDelivery.email.EmailSenderTask;
import com.example.dataDelivery.entity.*;
import com.example.dataDelivery.repository.CommentRepository;
import com.example.dataDelivery.repository.MyRepository;
import com.example.dataDelivery.temporary.IdGenerator;
import com.example.dataDelivery.email.EmailSender;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MyController {
    private final MyRepository contentRepository;
    private final MyRepository memberRepository;
    private final MyRepository commentRepository;
    private final MyRepository passwordResetTokenRepository;
    private final MyRepository reactionRepository;
    private final MyRepository commentReactionRepository;
    private Member logined = null;
    private String verificationCode;
    private String email;

    @Autowired
    public MyController(MyRepository contentRepository, MyRepository memberRepository, MyRepository commentRepository, MyRepository passwordResetTokenRepository, MyRepository reactionRepository, MyRepository commentReactionRepository) {
        this.contentRepository = contentRepository;
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.reactionRepository = reactionRepository;
        this.commentReactionRepository = commentReactionRepository;
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
            Optional<ArrayList<Reaction>> contentReactionsOptional = reactionRepository.findByContentId(id);
            Optional<ArrayList<CommentReaction>> commentReactionsOptional  = commentReactionRepository.findByContentId(id);



            Reaction findReaction = new Reaction(null,null,null,null);
            List<CommentReaction> findCommentReactionList = new ArrayList<>(); //회원의 댓글 반응만 담음


//           로그인한 회원의 아이디와 같은 리액션 찾기
            commentReactionsOptional.ifPresent(commentReactions  -> {
                commentReactions.stream()
                        // memberId가 logined와 같은 객체만 필터링합니다.
                        .filter(commentReaction -> commentReaction.getMemberId().equals(logined.getId()))
                        // 필터링된 객체를 새로운 리스트에 추가합니다.
                        .forEach(findCommentReactionList::add);
            });



            contentReactionsOptional.ifPresent(reactions -> {
                Optional<Reaction> foundReaction =  reactions.stream()
                        .filter(reaction -> reaction.getMemberId().equals(logined.getId()))
                        .findFirst();

                if (foundReaction.isPresent()) {
                    findReaction.setId(foundReaction.get().getId());
                    findReaction.setMemberId(foundReaction.get().getMemberId());
                    findReaction.setFavor(foundReaction.get().getFavor());
                }
            });



            if (findContent.isPresent()) {
                model.addAttribute("content", findContent.get());
                model.addAttribute("comments", findComments.get()); //이거 어떻게????


                //이 부분 고쳤으면...
                if(findReaction.getId() != null) {
                    model.addAttribute("reactionId", findReaction.getId());
                }
                //findReaction == null
                else {
                    Reaction reaction = new Reaction();
                    reaction.setId(IdGenerator.generatedRandomLong());
                    reaction.setMemberId(logined.getId());
                    reaction.setContentId(id);
                    reaction.setFavor(Favor.NEUTRAL);
                    reactionRepository.save(reaction);
                    model.addAttribute("reactionId", reaction.getId());
                }


                //commentReaction Model에 업데이트
                    model.addAttribute("commentReactions", findCommentReactionList);
                return "detail";
            } else {
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
        return "find-id";
    }


    //인증 메일 전송
    @PostMapping("/find-id")
    public String sendAuthEmail(String email) {
        //이메일에 해당하는 회원 존재 여부 확인 코드 추가?
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if(findMember.isPresent()) {
            System.out.println("이메일: " + email);
            this.email = email;
            this.verificationCode = IdGenerator.generatedRandomLong().toString();

            EmailSender emailSender = new EmailSender();
            EmailSenderTask task = new EmailSenderTask(email,"brian7536curry@gmail.com", "haqr ozsi vykg emhx", verificationCode,emailSender);
            new Thread(task).start();

            return "redirect:/find-id";
        } else{
            return "/";
        }
    }


    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam("verificationCode") String verificationCode,
                             Model model) {
        System.out.println("인증 코드: " + verificationCode + " 이메일: " + email);
        if(verificationCode.equals(this.verificationCode)) {
            // 인증 코드가 일치하는 경우
            Optional<Member> findMember = memberRepository.findByEmail(email); // 이메일로 회원 찾기
            Member member = findMember.get();
            model.addAttribute("userId", member.getId()); // 모델에 userId 추가
            return "verificationSuccess"; // 성공 시 페이지
        } else {
            return "redirect:/login"; // 실패 시 로그인 페이지로 리다이렉트
        }
    }



    @GetMapping("/find-password")
    public String findPassword() {
        return "find-password";
    }


    @PostMapping("/request-password-reset")
    public String sendResetLink(String email, HttpSession session) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isPresent() && findMember.get().getEmail().equals(email)) {
            // 비밀번호 재설정 토큰 생성
            String token = UUID.randomUUID().toString();

            // 토큰과 함께 비밀번호 재설정 링크를 이메일로 전송(이 링크에서 비밀번호 변경)
            String resetLink = "https://www.yourdomain.com/reset-password?token=" + "\n" + "임시 비밀번호: " + token;

            // 토큰을 데이터베이스에 저장 (여기에서는 토큰의 유효시간도 설정해야 함)
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setMemberId(findMember.get().getId());
            passwordResetToken.setExpiryDate(30); // 예를 들어 30분 후 만료

            //DB변경
            Member member = findMember.get();
            member.setPassword(token);
            memberRepository.update(member);     //비밀번호 변경 -> 저장
            passwordResetTokenRepository.save(passwordResetToken);

            // 이메일 서비스를 통해 재설정 링크를 포함한 이메일 전송
            EmailSender.sendEmail(email, "brian7536curry@gmail.com", "haqr ozsi vykg emhx", resetLink);


            return "passwordResetLinkSent";
        } else {
            // 해당 이메일의 사용자가 없는 경우
            return "userNotFound";
        }
    }



    //좋아요 개수 조정
    @PostMapping("/content/like/{id}")
    public String changeLikesCount(@PathVariable("id") Long id,
                                   @RequestParam("reactionId") Long reactionId) {   //reactionId안들어옴..
        if(reactionId != null) {
            //DB에서 Reaction 불러오기
            Optional<Reaction> reaction = reactionRepository.findById(reactionId); //html 수정해야 (Reaction.id도 넘겨주도록)
            Reaction findReaction = reaction.get();

            //DB에서 Content 불러오기
            Optional<Content> content = contentRepository.findById(id);
            Content findContent = content.get();

            //좋아요 취소
            if (findReaction.getFavor() == Favor.LIKE) {
                findReaction.setFavor(Favor.NEUTRAL);
                findContent.setLikesCount(findContent.getLikesCount() - 1);
            }
            //좋아요
            else {
                if(findReaction.getFavor() == Favor.DISLIKE) {
                    findContent.setDislikesCount(findContent.getDislikesCount() - 1);
                }
                findReaction.setFavor(Favor.LIKE);
                findContent.setLikesCount(findContent.getLikesCount() + 1);
            }
            //DB업데이트
            reactionRepository.update(findReaction);
            contentRepository.update(findContent);
            return "redirect:/details/" + findContent.getId();
        } else{
            System.out.println("null이다");
            return "redirect:/details/" + id;
        }
    }



    //싫어요 개수 조정
    @PostMapping("/content/dislike/{id}")
    public String changeDislikesCount(@PathVariable("id") Long id,
                                      @RequestParam("reactionId") Long reactionId) {
        if(reactionId != null) {
            //DB에서 Reaction 불러오기
            Optional<Reaction> reaction = reactionRepository.findById(reactionId); //html 수정해야 (Reaction.id도 넘겨주도록)
            Reaction findReaction = reaction.get();

            //DB에서 Content 불러오기
            Optional<Content> content = contentRepository.findById(id);
            Content findContent = content.get();

            //싫어요 취소
            if (findReaction.getFavor() == Favor.DISLIKE) {
                findReaction.setFavor(Favor.NEUTRAL);
                findContent.setDislikesCount(findContent.getDislikesCount() - 1);
            }
            //싫어요
            else {
                if(findReaction.getFavor() == Favor.LIKE) {
                    findContent.setLikesCount(findContent.getLikesCount() - 1);
                }
                findReaction.setFavor(Favor.DISLIKE);
                findContent.setDislikesCount(findContent.getDislikesCount() + 1);
            }
            //DB업데이트
            reactionRepository.update(findReaction);
            contentRepository.update(findContent);
            return "redirect:/details/" + findContent.getId();
        } else{
            System.out.println("null이다");
            return "redirect:/details/" + id;
        }
    }



    //댓글 좋아요 요청
    @PostMapping("/content/{contentId}/comments/like/{commentId}")
    public String changeCommentLikesCount(@PathVariable("contentId") Long contentId,
                                   @PathVariable("commentId") Long commentId) {   //reactionId안들어옴..


        Optional<ArrayList<CommentReaction>> commentReactionOptional = commentReactionRepository.findByCommentId(commentId);
        CommentReaction findCommentReaction = new CommentReaction(null,null,null,null,null);
        Optional<Comment> findComment = commentRepository.findById(commentId);


        commentReactionOptional.ifPresent(reactions -> {
            Optional<CommentReaction> foundReaction =  reactions.stream()
                    .filter(reaction -> reaction.getMemberId().equals(logined.getId()))
                    .findFirst();

            if (foundReaction.isPresent()) {
                findCommentReaction.setId(foundReaction.get().getId());
                findCommentReaction.setMemberId(foundReaction.get().getMemberId());
                findCommentReaction.setCommentId(foundReaction.get().getCommentId());
                findCommentReaction.setContentId(foundReaction.get().getContentId());
                findCommentReaction.setFavor(foundReaction.get().getFavor());
            }
        });






        if(findCommentReaction.getId() != null) {
            //좋아요 취소
            if (findCommentReaction.getFavor() == Favor.LIKE) {
                findCommentReaction.setFavor(Favor.NEUTRAL);
                findComment.get().setLikesCount(findComment.get().getLikesCount() - 1);
            }
            //좋아요
            else {
                //싫어요 -> 좋아요
                if(findCommentReaction.getFavor() == Favor.DISLIKE) {
                    findComment.get().setDislikesCount(findComment.get().getDislikesCount() - 1);
                }
                findCommentReaction.setFavor(Favor.LIKE);
                findComment.get().setLikesCount(findComment.get().getLikesCount() + 1);
            }


            //DB업데이트
            commentReactionRepository.update(findCommentReaction);
            commentRepository.update(findComment.get());
            return "redirect:/details/" + contentId;
            }
        else{
                //객체 생성
                CommentReaction commentReaction = new CommentReaction(logined.getId(),commentId,Favor.LIKE,contentId);
                Comment comment = findComment.get();
                comment.setLikesCount(comment.getLikesCount() + 1);

                commentRepository.update(comment);
                commentReactionRepository.save(commentReaction);
            return "redirect:/details/" + contentId;
        }
    }




    //댓글 싫어요 요청
    @PostMapping("/content/{contentId}/comments/dislike/{commentId}")
    public String changeCommentDislikesCount(@PathVariable("contentId") Long contentId,
                                          @PathVariable("commentId") Long commentId) {   //reactionId안들어옴..


        Optional<ArrayList<CommentReaction>> commentReactionOptional = commentReactionRepository.findByCommentId(commentId);
        CommentReaction findCommentReaction = new CommentReaction(null,null,null,null,null);
        Optional<Comment> findComment = commentRepository.findById(commentId);


        commentReactionOptional.ifPresent(reactions -> {
            Optional<CommentReaction> foundReaction =  reactions.stream()
                    .filter(reaction -> reaction.getMemberId().equals(logined.getId()))
                    .findFirst();

            if (foundReaction.isPresent()) {
                findCommentReaction.setId(foundReaction.get().getId());
                findCommentReaction.setMemberId(foundReaction.get().getMemberId());
                findCommentReaction.setCommentId(foundReaction.get().getCommentId());
                findCommentReaction.setContentId(foundReaction.get().getContentId());
                findCommentReaction.setFavor(foundReaction.get().getFavor());
            }
        });






        if(findCommentReaction.getId() != null) {
            //싫어요 취소
            if (findCommentReaction.getFavor() == Favor.DISLIKE) {
                findCommentReaction.setFavor(Favor.NEUTRAL);
                findComment.get().setDislikesCount(findComment.get().getDislikesCount() - 1);
            }
            //싫어요
            else {
                //좋아요 -> 싫어요
                if(findCommentReaction.getFavor() == Favor.LIKE) {
                    //좋아요 - 1
                    findComment.get().setLikesCount(findComment.get().getLikesCount() - 1);
                }
                findCommentReaction.setFavor(Favor.DISLIKE);
                //싫어요 + 1
                findComment.get().setDislikesCount(findComment.get().getDislikesCount() + 1);
            }


            //DB업데이트
            commentReactionRepository.update(findCommentReaction);
            commentRepository.update(findComment.get());
            return "redirect:/details/" + contentId;
        }
        else{
            //객체 생성
            CommentReaction commentReaction = new CommentReaction(logined.getId(),commentId,Favor.DISLIKE,contentId);
            Comment comment = findComment.get();
            comment.setDislikesCount(comment.getDislikesCount() + 1);

            commentRepository.update(comment);
            commentReactionRepository.save(commentReaction);
            return "redirect:/details/" + contentId;
        }
    }
}
