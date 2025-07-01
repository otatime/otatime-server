package com.otatime_server.user.domain;

import com.otatime_server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String password;
    private String username;
    private String profileImageUrl;
    private int adoptionCount;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String email, String password, String username, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.adoptionCount = 0;
        this.role = UserRole.USER;
    }

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void adopt() {
        this.adoptionCount++;
    }
}
