package com.otatime_server.post.repository;

import com.otatime_server.post.domain.Post;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query("SELECT p FROM Post p " +
            "WHERE p.startDate >= :today " +
            "ORDER BY p.startDate ASC")
    List<Post> findTop4ByStartDateClosest(@Param("today") LocalDate today);

}
