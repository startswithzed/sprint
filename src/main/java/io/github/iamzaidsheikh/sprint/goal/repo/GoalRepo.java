package io.github.iamzaidsheikh.sprint.goal.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.iamzaidsheikh.sprint.goal.model.Goal;

public interface GoalRepo extends MongoRepository<Goal, String>{
  List<Goal> findByInvCode(String invCode);
}
