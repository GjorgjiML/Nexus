package com.nexus.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class CommentView {
    Long id;
    String content;
    Instant createdAt;
    Long authorId;
    String authorUsername;
    String authorAvatarPath;
    boolean ownedByCurrentUser;
}
