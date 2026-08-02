package com.nexus.repository;

import com.nexus.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            WHERE c.post.id = :postId
            ORDER BY c.createdAt ASC
            """)
    List<Comment> findByPostIdWithAuthor(@Param("postId") Long postId);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author
            JOIN FETCH c.post
            WHERE c.id = :id
            """)
    Optional<Comment> findByIdWithAuthorAndPost(@Param("id") Long id);

    long countByPostId(Long postId);
}
