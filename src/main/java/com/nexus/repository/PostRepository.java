package com.nexus.repository;

import com.nexus.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
                    SELECT p FROM Post p
                    JOIN FETCH p.author
                    WHERE p.author.id = :authorId
                       OR p.author.id IN (
                            SELECT f.followee.id FROM Follow f WHERE f.follower.id = :authorId
                       )
                    ORDER BY p.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(p) FROM Post p
                    WHERE p.author.id = :authorId
                       OR p.author.id IN (
                            SELECT f.followee.id FROM Follow f WHERE f.follower.id = :authorId
                       )
                    """
    )
    Page<Post> findFeedForUser(@Param("authorId") Long authorId, Pageable pageable);

    @Query(
            value = """
                    SELECT p FROM Post p
                    JOIN FETCH p.author
                    WHERE p.author.id = :authorId
                    ORDER BY p.createdAt DESC
                    """,
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId"
    )
    Page<Post> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    java.util.Optional<Post> findByIdWithAuthor(@Param("id") Long id);
}
