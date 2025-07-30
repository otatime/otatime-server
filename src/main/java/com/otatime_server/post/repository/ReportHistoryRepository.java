package com.otatime_server.post.repository;

import com.otatime_server.post.domain.ReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
}
