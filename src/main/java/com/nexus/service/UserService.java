package com.nexus.service;

import com.nexus.dto.ProfileForm;
import com.nexus.dto.UserView;
import com.nexus.entity.User;
import com.nexus.exception.NotFoundException;
import com.nexus.repository.FollowRepository;
import com.nexus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserView toView(User user, Long currentUserId) {
        long followers = followRepository.countByIdFolloweeId(user.getId());
        long following = followRepository.countByIdFollowerId(user.getId());
        boolean own = currentUserId != null && currentUserId.equals(user.getId());
        boolean followed = !own && currentUserId != null
                && followRepository.existsByIdFollowerIdAndIdFolloweeId(currentUserId, user.getId());

        return UserView.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(own ? user.getEmail() : null)
                .bio(user.getBio())
                .avatarPath(user.getAvatarPath())
                .createdAt(user.getCreatedAt())
                .followerCount(followers)
                .followingCount(following)
                .followedByCurrentUser(followed)
                .ownProfile(own)
                .build();
    }

    @Transactional
    public User updateProfile(Long userId, ProfileForm form) {
        User user = getById(userId);
        if (form.getBio() != null) {
            user.setBio(form.getBio().trim());
        }
        if (form.getAvatar() != null && !form.getAvatar().isEmpty()) {
            String oldAvatar = user.getAvatarPath();
            String path = fileStorageService.store(form.getAvatar(), "avatars");
            user.setAvatarPath(path);
            fileStorageService.deleteIfExists(oldAvatar);
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserView> listFollowers(String username, Long currentUserId) {
        User user = getByUsername(username);
        return followRepository.findFollowers(user.getId()).stream()
                .map(u -> toView(u, currentUserId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserView> listFollowing(String username, Long currentUserId) {
        User user = getByUsername(username);
        return followRepository.findFollowing(user.getId()).stream()
                .map(u -> toView(u, currentUserId))
                .toList();
    }
}
