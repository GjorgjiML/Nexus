package com.nexus.service;

import com.nexus.dto.CommentView;
import com.nexus.dto.LikeResponse;
import com.nexus.dto.PostForm;
import com.nexus.dto.PostView;
import com.nexus.entity.Like;
import com.nexus.entity.LikeId;
import com.nexus.entity.Post;
import com.nexus.entity.User;
import com.nexus.exception.ForbiddenException;
import com.nexus.exception.NotFoundException;
import com.nexus.repository.CommentRepository;
import com.nexus.repository.LikeRepository;
import com.nexus.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Transactional
    public Post create(Long authorId, PostForm form) {
        User author = userService.getById(authorId);
        String imagePath = fileStorageService.store(form.getImage(), "posts");
        Post post = Post.builder()
                .author(author)
                .content(form.getContent().trim())
                .imagePath(imagePath)
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public void delete(Long postId, Long currentUserId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only delete your own posts");
        }
        fileStorageService.deleteIfExists(post.getImagePath());
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<PostView> getFeed(Long userId, int page, int size) {
        Page<Post> posts = postRepository.findFeedForUser(userId, PageRequest.of(page, size));
        return enrichPage(posts, userId, true);
    }

    @Transactional(readOnly = true)
    public Page<PostView> getProfilePosts(String username, Long currentUserId, int page, int size) {
        User author = userService.getByUsername(username);
        Page<Post> posts = postRepository.findByAuthorId(author.getId(), PageRequest.of(page, size));
        return enrichPage(posts, currentUserId, true);
    }

    @Transactional(readOnly = true)
    public PostView getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        return toView(post, currentUserId, true);
    }

    @Transactional
    public LikeResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        boolean liked;
        if (likeRepository.existsByIdUserIdAndIdPostId(userId, postId)) {
            likeRepository.deleteByIdUserIdAndIdPostId(userId, postId);
            liked = false;
        } else {
            User user = userService.getById(userId);
            Like like = Like.builder()
                    .id(new LikeId(userId, postId))
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
            liked = true;
        }
        return LikeResponse.builder()
                .liked(liked)
                .likeCount(likeRepository.countByIdPostId(postId))
                .build();
    }

    private Page<PostView> enrichPage(Page<Post> posts, Long currentUserId, boolean includeComments) {
        List<Post> content = posts.getContent();
        if (content.isEmpty()) {
            return posts.map(p -> toView(p, currentUserId, includeComments));
        }

        List<Long> postIds = content.stream().map(Post::getId).toList();
        Map<Long, Long> likeCounts = toCountMap(likeRepository.countByPostIds(postIds));
        Set<Long> likedIds = currentUserId == null
                ? Collections.emptySet()
                : likeRepository.findLikedPostIds(currentUserId, postIds);

        Map<Long, List<CommentView>> commentsByPost = new HashMap<>();
        Map<Long, Long> commentCounts = new HashMap<>();
        if (includeComments) {
            for (Long postId : postIds) {
                List<CommentView> comments = loadComments(postId, currentUserId);
                commentsByPost.put(postId, comments);
                commentCounts.put(postId, (long) comments.size());
            }
        } else {
            for (Long postId : postIds) {
                commentCounts.put(postId, commentRepository.countByPostId(postId));
            }
        }

        return posts.map(post -> PostView.builder()
                .id(post.getId())
                .content(post.getContent())
                .imagePath(post.getImagePath())
                .createdAt(post.getCreatedAt())
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .authorAvatarPath(post.getAuthor().getAvatarPath())
                .likeCount(likeCounts.getOrDefault(post.getId(), 0L))
                .commentCount(commentCounts.getOrDefault(post.getId(), 0L))
                .likedByCurrentUser(likedIds.contains(post.getId()))
                .ownedByCurrentUser(currentUserId != null && currentUserId.equals(post.getAuthor().getId()))
                .comments(commentsByPost.getOrDefault(post.getId(), List.of()))
                .build());
    }

    private PostView toView(Post post, Long currentUserId, boolean includeComments) {
        long likes = likeRepository.countByIdPostId(post.getId());
        long comments = commentRepository.countByPostId(post.getId());
        boolean liked = currentUserId != null
                && likeRepository.existsByIdUserIdAndIdPostId(currentUserId, post.getId());

        return PostView.builder()
                .id(post.getId())
                .content(post.getContent())
                .imagePath(post.getImagePath())
                .createdAt(post.getCreatedAt())
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .authorAvatarPath(post.getAuthor().getAvatarPath())
                .likeCount(likes)
                .commentCount(comments)
                .likedByCurrentUser(liked)
                .ownedByCurrentUser(currentUserId != null && currentUserId.equals(post.getAuthor().getId()))
                .comments(includeComments ? loadComments(post.getId(), currentUserId) : List.of())
                .build();
    }

    private List<CommentView> loadComments(Long postId, Long currentUserId) {
        return commentRepository.findByPostIdWithAuthor(postId).stream()
                .map(c -> CommentView.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .authorId(c.getAuthor().getId())
                        .authorUsername(c.getAuthor().getUsername())
                        .authorAvatarPath(c.getAuthor().getAvatarPath())
                        .ownedByCurrentUser(currentUserId != null && currentUserId.equals(c.getAuthor().getId()))
                        .build())
                .toList();
    }

    private Map<Long, Long> toCountMap(List<Object[]> rows) {
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((Long) row[0], (Long) row[1]);
        }
        return map;
    }
}
