package org.example.teampack.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.teampack.dto.UserDto;
import org.example.teampack.dto.UserProfileImageDto;
import org.example.teampack.service.UserEmailService;
import org.example.teampack.service.UserProfileImageService;
import org.example.teampack.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserEmailService userEmailService;
    private final UserProfileImageService profileImageService;

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
        UserDto loginUser = userService.findByEmail(userDto.getUserEmail());
        session.setAttribute("loginUser", loginUser);

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

    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/user/login";
        }

        //  최신 정보로 다시 조회
        UserDto fullUserInfo = userService.getMyPageInfo(loginUser.getUserEmail());
        model.addAttribute("user", fullUserInfo);

        // 프로필 이미지 조회 추가
        UserProfileImageDto image = profileImageService.getProfileImage(loginUser.getUserId());
        if (image != null) {
            model.addAttribute("profileImageUrl", "/uploads/" + image.getUserImageUrl()); // ✅ 경로 보정
        } else {
            model.addAttribute("profileImageUrl", null);
        }

        return "user/mypage";
    }


    //회원 정보 수정
    @GetMapping("/edit")
    public String showEditForm(HttpSession session, Model model){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        UserDto user = userService.findByEmail(loginUser.getUserEmail());
        model.addAttribute("user",user);
        return "user/edit-profile";
    }

    @PostMapping("/update")
    public String updateUserInfo(@ModelAttribute UserDto userDto, HttpSession session){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        //기존 로그인된 이메일 기준으로 수정
        userDto.setUserId(loginUser.getUserId());
        userService.updateUser(userDto);

        //세션 정보 갱신
        session.setAttribute("loginUser", userService.findByEmail(loginUser.getUserEmail()));

        return "redirect:/user/mypage";
    }

    // 기존 비밀번호 확인 폼
    @GetMapping("/change-password")
    public String showChangePasswordForm(){
        return "user/change-password-step1";
    }

    // 기존 비밀번호 검증
    @PostMapping("/verify-password")
    public String verifyCurrentPassword(@RequestParam String currentPassword,
                                        HttpSession session, Model model){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if(loginUser == null) return "redirect:/user/login";

        UserDto user = userService.findById(loginUser.getUserId());
        if(!user.getUserPassword().equals(currentPassword)){
            model.addAttribute("error","비밀번호가 일치하지 않습니다.");
            return "user/change-password-step1";
        }
        return "user/change-password-step2";

    }
    @PostMapping("/update-password")
    public String updatePassword(@RequestParam String newPassword, HttpSession session){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if(loginUser == null) return "redirect:user/login";

        userService.updatePassword(loginUser.getUserId(), newPassword);
        return "redirect:/user/mypage";
    }


    @PostMapping("/upload-profile-image")
    public String uploadProfileImage(@RequestParam("image")MultipartFile file, HttpSession session){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        profileImageService.uploadProfileImage(loginUser.getUserId(), file);

        return "redirect:/user/mypage";
    }

    //이미지 수정
    @PostMapping("/update-profile-image")
    public String updateProfileImage(@RequestParam("image") MultipartFile file, HttpSession session){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        profileImageService.uploadOrUpdateProfileImage(loginUser.getUserId(), file);
        return "redirect:/user/mypage";
    }

    //프로필 이미지 삭제
    @PostMapping("/delete-profile-image")
    public String deleteProfileImage(HttpSession session){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if(loginUser == null) return "redirect:/user/login";

        profileImageService.deleteProfileImage(loginUser.getUserId());
        return "redirect:/user/mypage";
    }




}
