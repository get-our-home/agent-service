package com.getourhome.agentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Setter
    @Column(nullable = false)
    private String username;

    @Setter
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Setter
    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;
    
    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "registration_status", nullable = false)
    private RegistrationStatus registrationStatus = RegistrationStatus.PENDING;

    @Setter
    @Column(name = "reject_reason")
    private String rejectReason;

    @PrePersist
    protected void onCreate() {
        if (registrationStatus == null) {
            registrationStatus = RegistrationStatus.PENDING;
        }
    }
}

