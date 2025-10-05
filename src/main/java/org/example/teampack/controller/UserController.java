package org.example.teampack.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.teampack.dto.UserDto;
import org.example.teampack.service.UserEmailService;
import org.example.teampack.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserEmailService userEmailService;

    //인증번호 검증 창
    @GetMapping("/email")
    public String emailForm(){
        return "user/email-form";
    }

    @PostMapping("/sendCode")
    public String sendCode(@RequestParam String email, Model model){
        if(userService.findByEmail(email) != null){
            model.addAttribute("error","이미 가입된 이메일입니다.");
            return "user/email-form";
        }

        userEmailService.sendVerificationCode(email);
        model.addAttribute("email",email);
        return  "user/verify-code";
    }


    @PostMapping("/checkCode")
    public String checkCode(@RequestParam String email, @RequestParam String code,Model model){
        boolean isVerified = userEmailService.verifyCode(email, code);
        if(isVerified){
            model.addAttribute("userDto", new UserDto());
            model.addAttribute("email",email);
            return "user/join";
        }else{
            model.addAttribute("error","인증번호가 일치하지 않습니다.");
            model.addAttribute("email",email);
            return "user/verify-code";
        }
    }


    //회원가입 폼 페이지
    @GetMapping("/join")
    public String showJoinForm(){
        return "user/join";
    }

    //회원 가입 처리
    @PostMapping("/join")
    public String join(@ModelAttribute UserDto userDto, HttpSession session, Model model) {
        boolean result = userService.register(userDto);
        if (!result) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            return "user/join";
        }

        //회원가입 성공 후 자동 로그인
        session.setAttribute("loginUser", userDto);

        // 초대 토큰 처리
        String token = (String) session.getAttribute("pendingInviteToken");
        if(token!= null){
            session.removeAttribute("pendingInviteToken");
            return "redirect:/team/invite?token="+token;
        }

        return "redirect:/user/join-success";
    }

    //가입 성공 화면
    @GetMapping("/join-success")
    public String joinSuccess(){
        return "user/join-success";
    }

    //로그인 폼
    @GetMapping("/login")
    public String showLoginForm(){
        return "user/login";
    }

    //로그인 처리
    @PostMapping("/login")
    public  String login(@RequestParam String userEmail,
                         @RequestParam String userPassword,
                         HttpSession session,
                         Model model){
        UserDto user = userService.findByEmail(userEmail);

        if (user == null || !user.getUserPassword().equals(userPassword)){
            model.addAttribute("error","이메일 또는 비밀번호가 일치하지 않습니다.");
            return  "user/login";
        }

    // 로그인 성공
        session.setAttribute("loginUser",user);

        //초대 토큰이 있다면 해당 팀 초대 수락 경로로 리다이렉트
        String token = (String) session.getAttribute("pendingInviteToken");
        if(token != null){
            session.removeAttribute("pendingInviteToken");
            return "redirect:/team/invite?token="+token;
        }
        return "redirect:/";
    }

    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/user/login";
    }

}
