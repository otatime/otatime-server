package com.otatime_server.user.repository;

import com.otatime_server.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Modifying(flushAutomatically = true)
    @Query("update User u set u.username = :username where u.id = :userId")
    void updateUsername(@Param("username") String username, @Param("userId") Long userId);

    @Modifying(flushAutomatically = true)
    @Query("update User u set u.profileImageUrl = :profileImageUrl where u.id = :userId")
    void updateProfileImage(@Param("profileImageUrl") String profileImage, @Param("userId") Long id);
}
