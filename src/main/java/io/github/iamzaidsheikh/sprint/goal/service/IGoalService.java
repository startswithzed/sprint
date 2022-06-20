package io.github.iamzaidsheikh.sprint.goal.service;

import java.util.List;

import io.github.iamzaidsheikh.sprint.goal.dto.GoalDTO;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;

public interface IGoalService {
  List<Goal> getAllGoals();

  Goal getGoal(String goalId);

  String createGoal(String username, GoalDTO data);

  String invite(String goalId, String username);

  String join(String invCode, String username);
}
