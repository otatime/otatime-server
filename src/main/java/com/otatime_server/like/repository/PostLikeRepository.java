package com.otatime_server.like.repository;

import com.otatime_server.like.domain.PostLike;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("select pl from PostLike pl where pl.userId = :userId")
    Page<PostLike> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("select pl.postId from PostLike pl where pl.userId = :userId")
    List<Long> findPostIdByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

}
