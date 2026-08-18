package com.alireza.viewservice.dto;

import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean enabled;
    private Instant createdAt;
}
