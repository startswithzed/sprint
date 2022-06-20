package io.github.iamzaidsheikh.sprint.goal.service;

import java.util.List;

import io.github.iamzaidsheikh.sprint.goal.model.Goal;

public interface IGoalService {
  List<Goal> getAllGoals();

  Goal getGoal(String goalId);
}
