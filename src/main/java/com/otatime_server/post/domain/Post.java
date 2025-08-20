package com.otatime_server.post.domain;

import com.otatime_server.post.dto.PostUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String summary;
    private String details;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    public Post(String title, String summary, String details, LocalDate startDate, LocalDate endDate, String location, String imageUrl,
                Region region, EventStatus eventStatus, Category category, EventType eventType, PostStatus postStatus) {
        this.title = title;
        this.summary = summary;
        this.details = details;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.imageUrl = imageUrl;
        this.region = region;
        this.eventStatus = eventStatus;
        this.category = category;
        this.eventType = eventType;
        this.postStatus = postStatus;
    }

    public Long update(PostUpdateRequest postUpdateRequest) {

        this.title = postUpdateRequest.title();
        this.summary = postUpdateRequest.summary();
        this.details = postUpdateRequest.details();
//        this.category;
        
        return this.id;
    }
}
