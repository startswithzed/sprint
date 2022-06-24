package io.github.iamzaidsheikh.sprint.auth.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.iamzaidsheikh.sprint.auth.model.User;

public interface UserRepo extends MongoRepository<User, String> {
  User findByUsername(String username);
}
