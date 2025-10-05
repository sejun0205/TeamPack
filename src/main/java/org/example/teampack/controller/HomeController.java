package org.example.teampack.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.teampack.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model){
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if(loginUser != null){
            model.addAttribute("loginUser",loginUser);
        }
        return  "home";
    }
}
