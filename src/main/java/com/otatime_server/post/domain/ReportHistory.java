package com.otatime_server.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_hostory_id")
    private Long id;

    private Long reporterId;
    private Long postId;

    public ReportHistory(Long reporterId, Long postId) {
        this.reporterId = reporterId;
        this.postId = postId;
    }
}
