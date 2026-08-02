package com.nexus.service;

import com.nexus.dto.CommentForm;
import com.nexus.dto.CommentView;
import com.nexus.entity.Comment;
import com.nexus.entity.Post;
import com.nexus.entity.User;
import com.nexus.exception.ForbiddenException;
import com.nexus.exception.NotFoundException;
import com.nexus.repository.CommentRepository;
import com.nexus.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public CommentView addComment(Long postId, Long authorId, CommentForm form) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        User author = userService.getById(authorId);
        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(form.getContent().trim())
                .build();
        Comment saved = commentRepository.save(comment);
        return CommentView.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .authorId(author.getId())
                .authorUsername(author.getUsername())
                .authorAvatarPath(author.getAvatarPath())
                .ownedByCurrentUser(true)
                .build();
    }

    @Transactional
    public void delete(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findByIdWithAuthorAndPost(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only delete your own comments");
        }
        commentRepository.delete(comment);
    }
}
