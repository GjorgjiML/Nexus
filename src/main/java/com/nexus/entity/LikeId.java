package com.nexus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LikeId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "post_id")
    private Long postId;
}
