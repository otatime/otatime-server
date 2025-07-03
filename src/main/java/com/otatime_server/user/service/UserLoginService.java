package com.otatime_server.user.service;

import com.otatime_server.event.email.EmailAuthEvent;
import com.otatime_server.refreshtoken.RefreshToken;
import com.otatime_server.refreshtoken.RefreshTokenRepository;
import com.otatime_server.user.domain.User;
import com.otatime_server.user.dto.EmailVo;
import com.otatime_server.user.dto.JoinRequest;
import com.otatime_server.user.dto.LoginRequest;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher publisher;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponse join(JoinRequest joinRequest) {
        checkEmail(joinRequest.email());
        checkUsername(joinRequest.username());
        User user = new User(
                joinRequest.email(),
                joinRequest.password(),
                joinRequest.username(),
                joinRequest.profileImageUrl()
        );

        User savedUser = userRepository.save(user);

        savedUser.encodePassword(encoder.encode(savedUser.getPassword()));

        publisher.publishEvent(new EmailAuthEvent(savedUser.getId(), savedUser.getEmail()));

        return new UserResponse(savedUser.getId());
    }

    private void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
    }

    private void checkUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 이름 입니다.");
        }
    }

    public EmailVo login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일 또는 비밀번호가 잘못되었습니다."));
        if (!encoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("해당 이메일 또는 비밀번호가 잘못되었습니다.");
        }
        return new EmailVo(user.getEmail());
    }

    @Transactional
    public EmailVo findEmailByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("해당 토큰은 없습니다."));
        String email = refreshToken.getEmail();
        refreshTokenRepository.delete(refreshToken);
        return new EmailVo(email);
    }

    public void logout(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow();
        refreshTokenRepository.delete(refreshToken);
    }
}
