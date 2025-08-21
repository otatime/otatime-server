package com.otatime_server.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(length = 10, nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String street;

    @Column
    private String details;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @OneToOne(mappedBy = "address")
    private Post post;

    public Address(String zipCode, String street, String details, Double latitude, Double longitude) {
        this.zipCode = zipCode;
        this.street = street;
        this.details = details;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void add(Post post) {
        this.post = post;
    }
}
