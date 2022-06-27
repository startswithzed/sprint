package io.github.iamzaidsheikh.sprint.profile.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;

public interface UserProfileRepo extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUsername(String username);
}
