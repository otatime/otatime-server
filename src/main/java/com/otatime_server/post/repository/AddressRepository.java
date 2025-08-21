package com.otatime_server.post.repository;

import com.otatime_server.post.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
