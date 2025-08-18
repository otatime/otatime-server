package com.otatime_server.user.service;

import com.otatime_server.like.domain.PostLike;
import com.otatime_server.like.repository.PostLikeRepository;
import com.otatime_server.user.domain.User;
import com.otatime_server.user.dto.MyPageResponse;
import com.otatime_server.user.dto.UpdateProfileImageVO;
import com.otatime_server.user.dto.UpdateUsernameVO;
import com.otatime_server.user.dto.UserResponse;
import com.otatime_server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    public MyPageResponse getMyPage(String userEmail) {
        User user = getUser(userEmail);
        return new MyPageResponse(user.getId(), user.getUsername(), user.getProfileImageUrl(), user.getAdoptionCount());
    }

    @Transactional
    public UserResponse updateUsername(String userEmail, UpdateUsernameVO updateUsernameVO) {
        checkUsername(updateUsernameVO.username());
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

    public Page<PostLike> getPostLikes(String email) {
        User user = getUser(email);

        return postLikeRepository.findByUserId(
                user.getId(),
                PageRequest.of(0, 10, Sort.by("post_like_id").descending())
        );
    }


    private void checkUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 이름 입니다.");
        }
    }

    private User getUser(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));
    }

    public Page<User> getUserInfo() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());
        return userRepository.findAll(pageRequest);
    }

    @Transactional
    public Long deleteUser(Long userId) {
        userRepository.deleteById(userId);
        return userId;
    }
}
