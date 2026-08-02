package com.nexus.repository;

import com.nexus.entity.Follow;
import com.nexus.entity.FollowId;
import com.nexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    boolean existsByIdFollowerIdAndIdFolloweeId(Long followerId, Long followeeId);

    void deleteByIdFollowerIdAndIdFolloweeId(Long followerId, Long followeeId);

    long countByIdFollowerId(Long followerId);

    long countByIdFolloweeId(Long followeeId);

    @Query("""
            SELECT f.follower FROM Follow f
            WHERE f.followee.id = :userId
            ORDER BY f.createdAt DESC
            """)
    List<User> findFollowers(@Param("userId") Long userId);

    @Query("""
            SELECT f.followee FROM Follow f
            WHERE f.follower.id = :userId
            ORDER BY f.createdAt DESC
            """)
    List<User> findFollowing(@Param("userId") Long userId);
}
