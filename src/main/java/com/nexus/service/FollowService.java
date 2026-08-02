package com.nexus.service;

import com.nexus.entity.Follow;
import com.nexus.entity.FollowId;
import com.nexus.entity.User;
import com.nexus.exception.BadRequestException;
import com.nexus.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    @Transactional
    public void follow(Long followerId, String followeeUsername) {
        User followee = userService.getByUsername(followeeUsername);
        if (followee.getId().equals(followerId)) {
            throw new BadRequestException("You cannot follow yourself");
        }
        if (followRepository.existsByIdFollowerIdAndIdFolloweeId(followerId, followee.getId())) {
            return;
        }
        User follower = userService.getById(followerId);
        Follow follow = Follow.builder()
                .id(new FollowId(followerId, followee.getId()))
                .follower(follower)
                .followee(followee)
                .build();
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Long followerId, String followeeUsername) {
        User followee = userService.getByUsername(followeeUsername);
        followRepository.deleteByIdFollowerIdAndIdFolloweeId(followerId, followee.getId());
    }
}
