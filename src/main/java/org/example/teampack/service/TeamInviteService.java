package org.example.teampack.service;

public interface TeamInviteService {
    void sendInvite(String email, Long teamId, String role);
    boolean acceptInvite(String token, Long userId,String loginEmail);

}
