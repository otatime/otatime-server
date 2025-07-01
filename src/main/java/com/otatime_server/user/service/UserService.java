package com.otatime_server.user.service;

import com.otatime_server.user.domain.User;
import com.otatime_server.user.dto.MyPageResponse;
import com.otatime_server.user.dto.UpdateProfileImageVO;
import com.otatime_server.user.dto.UpdateUsernameVO;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public MyPageResponse getMyPage(String userEmail) {
        User user = getUser(userEmail);
        return new MyPageResponse(user.getId(), user.getUsername(), user.getProfileImageUrl(), user.getAdoptionCount());
    }

    @Transactional
    public UserResponse updateUsername(String userEmail, UpdateUsernameVO updateUsernameVO) {
        User user = getUser(userEmail);
        userRepository.updateUsername(updateUsernameVO.username(), user.getId());
        return new UserResponse(user.getId());
    }

    @Transactional
    public UserResponse updateProfileImage(String userEmail, UpdateProfileImageVO updateProfileImageVO) {
        User user = getUser(userEmail);
        userRepository.updateProfileImage(updateProfileImageVO.profileImageUrl(), user.getId());
        return new UserResponse(user.getId());
    }

    private User getUser(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    }

}
