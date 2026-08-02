package com.nexus.repository;

import com.nexus.entity.Like;
import com.nexus.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    boolean existsByIdUserIdAndIdPostId(Long userId, Long postId);

    long countByIdPostId(Long postId);

    void deleteByIdUserIdAndIdPostId(Long userId, Long postId);

    @Query("SELECT l.id.postId FROM Like l WHERE l.id.userId = :userId AND l.id.postId IN :postIds")
    Set<Long> findLikedPostIds(@Param("userId") Long userId, @Param("postIds") Collection<Long> postIds);

    @Query("SELECT l.id.postId AS postId, COUNT(l) AS cnt FROM Like l WHERE l.id.postId IN :postIds GROUP BY l.id.postId")
    List<Object[]> countByPostIds(@Param("postIds") Collection<Long> postIds);
}
