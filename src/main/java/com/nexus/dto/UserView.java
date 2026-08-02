package com.nexus.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class UserView {
    Long id;
    String username;
    String email;
    String bio;
    String avatarPath;
    Instant createdAt;
    long followerCount;
    long followingCount;
    boolean followedByCurrentUser;
    boolean ownProfile;
}
