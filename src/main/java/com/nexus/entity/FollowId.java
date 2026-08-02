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
public class FollowId implements Serializable {

    @Column(name = "follower_id")
    private Long followerId;

    @Column(name = "followee_id")
    private Long followeeId;
}
