package com.notificationengine.client.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="clients")
public class Client {
    @Id
    // UUID application level par automatically generate hoga
    @GeneratedValue(strategy = GenerationType.UUID )
    private UUID id;

    @Column(nullable = false)
    private String name ;
    @Column(nullable = false,unique = true)
    private String slug;

    private String email;

    // Client ka lifecycle status define karta hai
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Client last update hone ka timestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate(){
        Instant now= Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = Instant.now();

    }
}

