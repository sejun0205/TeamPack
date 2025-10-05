package org.example.teampack.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.teampack.service.UserEmailService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserEmailServiceImpl implements UserEmailService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    private static final long EXPIRATION_TIME =3L; //3분

    @Override
    public void sendVerificationCode(String email) {
        String code =createCode();
        redisTemplate.opsForValue().set(email, code,EXPIRATION_TIME, TimeUnit.MINUTES);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[TeamPack] 이메일 인증 코드");
        message.setText("인증번호:"+code+"\n 유효시간: 3분");

        mailSender.send(message);

    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);
        return code.equals(storedCode);
    }


    private String createCode(){
        return  String.format("%06d", new Random().nextInt(999999));
    }
}
