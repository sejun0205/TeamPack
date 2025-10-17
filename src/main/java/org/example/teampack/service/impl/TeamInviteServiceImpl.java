package org.example.teampack.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.teampack.dao.TeamDao;
import org.example.teampack.dto.MembersDto;
import org.example.teampack.dto.TeamDto;
import org.example.teampack.service.TeamInviteService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TeamInviteServiceImpl implements TeamInviteService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final TeamDao teamDao;

    private static final long TTL_MINUTES = 60;

    @Override
    public void sendInvite(String email, Long teamId, String role) {

        //팀 상태 조회
        TeamDto team = teamDao.selectTeamById(teamId);
        if(team == null) {
            throw new RuntimeException("해당 팀을 찾을 수 없습니다.");
        }

        //종료된 팀은 초대 불가
        if (team.getClosedAt() != null){
            throw new IllegalStateException("이미 마감된 팀에는 팀원을 초대할 수 없습니다.");
        }


        //초대 토큰 생성 및 레디스 저장
        String token = UUID.randomUUID().toString();
        // "::"로 구분하여 저장 (":"는 이메일에 포함될 수 있음 → 안전한 구분자 사용)
        String value = teamId + "::" + email + "::" + role;
        redisTemplate.opsForValue().set("invite:" + token, value, TTL_MINUTES, TimeUnit.MINUTES);

        //초대 링크 생성
        String link = "http://localhost:8080/team/invite?token=" + token;

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[TeamPack] 팀 초대 링크");

            String html = "<p>아래 링크를 클릭하여 팀에 참여하세요:</p>"
                    + "<p><a href=\"" + link + "\">" + link + "</a></p>";

            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 오류 발생", e);
        }
    }

    @Override
    public boolean acceptInvite(String token, Long userId, String loginEmail) {
        String key = "invite:" + token;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return false;

        // "::"로 split
        String[] parts = value.split("::");
        Long teamId = Long.valueOf(parts[0]);
        String invitedEmail = parts[1];
        String role = parts[2];

        // 이메일 일치 검증
        if (!invitedEmail.equalsIgnoreCase(loginEmail)) {
            return false;
        }

        MembersDto member = new MembersDto();
        member.setUserId(userId);
        member.setTeamId(teamId);
        member.setMemberType("MEMBER");
        member.setMemberRole(role);

        teamDao.insertMember(member);
        redisTemplate.delete(key);
        return true;
    }
}
