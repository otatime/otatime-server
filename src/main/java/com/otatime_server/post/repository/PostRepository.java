package com.otatime_server.post.repository;

import com.otatime_server.post.domain.Post;
import com.otatime_server.post.domain.PostStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("SELECT p FROM Post p " +
            "WHERE p.startDate >= :today " +
            "ORDER BY p.startDate ASC")
    List<Post> findTop4ByStartDateClosest(@Param("today") LocalDate today);


    @Query("select p from Post p where p.postStatus = :status")
    Page<Post> findAllByStatus(Pageable pageable, @Param("status") PostStatus status);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Post p set p.postStatus = 'POSTED' where p.id = :postId")
    void updatePostStatus(@Param("postId") Long postId);
}
