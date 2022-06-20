package io.github.iamzaidsheikh.sprint.goal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.repo.GoalRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService implements IGoalService {

  private final GoalRepo gr;

  @Override
  public List<Goal> getAllGoals() {
    log.info("Fetching all goals");

    return gr.findAll();
  }

  @Override
  public Goal getGoal(String goalId) {
    log.info("Fetching goal: {}", goalId);
    var go = gr.findById(goalId);
    if (!go.isPresent()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    } else {
      return go.get();
    }
  }

}
