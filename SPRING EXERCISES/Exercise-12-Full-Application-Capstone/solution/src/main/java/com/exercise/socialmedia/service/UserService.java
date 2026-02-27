package com.exercise.socialmedia.service;

import com.exercise.socialmedia.annotation.Audited;
import com.exercise.socialmedia.dto.UserProfileResponse;
import com.exercise.socialmedia.entity.User;
import com.exercise.socialmedia.exception.ResourceNotFoundException;
import com.exercise.socialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toProfileResponse(user);
    }

    @Audited(action = "FOLLOW_USER")
    @Transactional
    public void follow(Long targetId, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));
        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("User", targetId));
        if (currentUser.getId().equals(targetId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        targetUser.getFollowers().add(currentUser);
        userRepository.save(targetUser);
    }

    @Transactional
    public void unfollow(Long targetId, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));
        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("User", targetId));
        targetUser.getFollowers().remove(currentUser);
        userRepository.save(targetUser);
    }

    private UserProfileResponse toProfileResponse(User user) {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setBio(user.getBio());
        r.setCreatedAt(user.getCreatedAt());
        r.setFollowersCount(user.getFollowers().size());
        r.setFollowingCount(user.getFollowing().size());
        r.setPostsCount(user.getPosts().size());
        return r;
    }
}
