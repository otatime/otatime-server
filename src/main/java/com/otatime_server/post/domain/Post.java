package com.otatime_server.post.domain;

import com.otatime_server.post.dto.PostUpdateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    public Post(String title, String summary, String details, LocalDate startDate, LocalDate endDate, String imageUrl,
                Region region, EventStatus eventStatus, Category category, EventType eventType, PostStatus postStatus, Address address) {
        this.title = title;
        this.summary = summary;
        this.details = details;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.region = region;
        this.eventStatus = eventStatus;
        this.category = category;
        this.eventType = eventType;
        this.postStatus = postStatus;
        this.address = address;
        address.add(this);
    }

    public Long update(PostUpdateRequest postUpdateRequest) {

        this.title = postUpdateRequest.title();
        this.summary = postUpdateRequest.summary();
        this.details = postUpdateRequest.details();
//        this.category;
        
        return this.id;
    }
}
