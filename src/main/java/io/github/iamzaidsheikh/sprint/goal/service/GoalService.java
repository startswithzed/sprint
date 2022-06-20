package io.github.iamzaidsheikh.sprint.goal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.dto.GoalDTO;
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

  @Override
  public String createGoal(String username, GoalDTO data) {
    var goal = new Goal(
        username,
        LocalDateTime.parse(data.getDeadline()),
        data.getTitle(),
        data.getDesc());
    var goalId = gr.save(goal).getId();

    log.info("User: {} created a new goal: {}", username, goalId);
    return goalId;
  }

  @Override
  public String invite(String goalId, String username) {
    var go = gr.findById(goalId);
    if (!go.isPresent()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!goal.getAuthor().equals(username)) {
      log.error("User: {} is not the author of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not the author of goal: " + goalId);
    }
    if (goal.getInvCode() == null) {
      var invCode = RandomStringUtils.randomAlphanumeric(10);
      goal.setInvCode(invCode);
      log.info("Generating an invite code for goal: {}", goalId);

      return gr.save(goal).getInvCode();
    } else {
      log.info("Fetching invite code for goal: {}", goalId);

      return goal.getInvCode();
    }
  }

  @Override
  public String join(String invCode, String username) {
    var goals = gr.findByInvCode(invCode);
    if (goals.isEmpty()) {
      log.error("Could not find goal with invite code: {}", invCode);
      throw new ResourceNotFoundException("Could not find goal with invite code: " + invCode);
    }
    var goal = goals.get(0);
    var goalId = goal.getId();
    if (goal.getAuthor().equals(username)) {
      log.error("Author: {} can't join goal: {} as a mentor", username, goalId);
      throw new BadRequestException("Author: " + username + " can't join a goal as a mentor");
    }
    if (goal.getMentor1() != null && goal.getMentor2() != null) {
      log.error("Goal: {} already has two mentors", goalId);
      throw new BadRequestException("Goal: " + goalId + " already has two mentors");
    }
    if (username.equals(goal.getMentor1()) || username.equals(goal.getMentor2())) {
      log.error("User: {} is already a mentor for goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is already a mentor for goal: " + goalId);
    } else {
      if (goal.getMentor1() == null && goal.getMentor2() != null) {
        goal.setMentor1(username);
      } else if (goal.getMentor1() != null && goal.getMentor2() == null) {
        goal.setMentor2(username);
      } else {
        goal.setMentor1(username);
      }
    }
    log.info("User: {} added as a mentor for goal: {}", username, goalId);

    return gr.save(goal).getId();
  }

}
