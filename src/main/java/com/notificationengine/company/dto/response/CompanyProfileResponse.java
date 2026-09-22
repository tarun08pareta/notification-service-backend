package com.notificationengine.company.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {

    private UUID id;

    private String companyName;

    private String logoUrl;

    private String senderName;

    private String senderEmail;

    private String replyToEmail;

    private String smsSenderId;

    private String smsSenderNumber;

    private Instant createdAt;

    private Instant updatedAt;
}