package com.nexus.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LikeResponse {
    boolean liked;
    long likeCount;
}
