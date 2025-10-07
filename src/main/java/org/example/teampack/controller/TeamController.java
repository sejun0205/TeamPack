package org.example.teampack.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;
import org.example.teampack.dto.UserDto;
import org.example.teampack.service.TeamInviteService;
import org.example.teampack.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final TeamInviteService teamInviteService;

    // 팀 생성 폼
    @GetMapping("/create")
    public String showForm(Model model) {
        model.addAttribute("teamDto", new TeamDto());
        return "team/create";
    }

    // 팀 생성
    @PostMapping("/create")
    public String create(@ModelAttribute TeamDto teamDto, @RequestParam String myRole, HttpSession session) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        teamService.createTeamAndLeader(teamDto, loginUser.getUserId(), myRole);
        return "redirect:/";
    }

    // 초대 메일 요청 받기
    @PostMapping("/invite")
    public String inviteMember(@RequestParam String email, @RequestParam Long teamId,
                               @RequestParam String role, HttpSession session) {

        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        teamInviteService.sendInvite(email, teamId, role);
        return "redirect:/team/detail/" + teamId;
    }

    // 초대 수락 링크 처리
    @GetMapping("/invite")
    public String handleInvite(@RequestParam String token, HttpSession session) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");

        if (loginUser == null) {
            session.setAttribute("pendingInviteToken", token);
            return "redirect:/user/login";
        }

        boolean success = teamInviteService.acceptInvite(token, loginUser.getUserId(), loginUser.getUserEmail());
        return success ? "redirect:/team/my" : "invite/invalid";
    }

    @GetMapping("/invite-form")
    public String showInviteForm(@RequestParam Long teamId, Model model) {
        model.addAttribute("teamId", teamId);
        return "team/invite-form";
    }

    @GetMapping("/detail/{id}")
    public String teamDetail(@PathVariable Long id, Model model, HttpSession session) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        // 수정된 service 메서드 사용
        TeamDto team = teamService.getTeamByIdWithMemberType(id, loginUser.getUserId());
        List<MembersDto> members = teamService.getMembersByTeamId(id);

        model.addAttribute("team", team);
        model.addAttribute("members",members);
        return "team/detail";
    }

    @GetMapping("/my")
    public String myTeamPage(Model model, HttpSession session) {
        UserDto loginUser = (UserDto) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/user/login";

        List<TeamDto> myTeams = teamService.getTeamByUserId(loginUser.getUserId());
        model.addAttribute("teams", myTeams);

        return "team/my";
    }
}
