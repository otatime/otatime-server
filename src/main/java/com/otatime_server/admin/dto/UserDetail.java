package com.otatime_server.admin.dto;

import com.otatime_server.user.domain.User;

public record UserDetail(
        Long userId,
        String name,
        String email,
        int adoptionCount
) {

    public static UserDetail of(User user) {
        return new UserDetail(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAdoptionCount()
        );
    }

}
