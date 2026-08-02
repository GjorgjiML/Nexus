package com.nexus.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class PostView {
    Long id;
    String content;
    String imagePath;
    Instant createdAt;
    Long authorId;
    String authorUsername;
    String authorAvatarPath;
    long likeCount;
    long commentCount;
    boolean likedByCurrentUser;
    boolean ownedByCurrentUser;
    List<CommentView> comments;
}
