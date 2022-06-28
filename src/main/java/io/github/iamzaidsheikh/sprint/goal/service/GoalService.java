package io.github.iamzaidsheikh.sprint.goal.service;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.dto.GoalDTO;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.model.GoalStatus;
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
        Instant.parse(data.getDeadline()),
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
    if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
      log.error("Goal: {} is already completed", goalId);
      throw new BadRequestException("Goal: " + goalId + " is already completed");
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
    if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
      log.error("Goal: {} is already completed", goalId);
      throw new BadRequestException("Goal: " + goalId + " is already completed");
    }
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

  @Override
  public String leaveGoal(String goalId, String username) {
    var go = gr.findById(goalId);
    if (!go.isPresent()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
      log.error("Goal: {} is already completed", goalId);
      throw new BadRequestException("Goal: " + goalId + " is already completed");
    }
    if (!username.equals(goal.getMentor1()) && !username.equals(goal.getMentor2())) {
      log.error("User: {} is not a mentor of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not a mentor of goal: " + goalId);
    }
    if (username.equals(goal.getMentor1())) {
      goal.setMentor1(null);
    } else if (username.equals(goal.getMentor2())) {
      goal.setMentor2(null);
    }
    log.info("User: {} is no longer a mentor of goal: {}", username, goalId);
    return gr.save(goal).getId();
  }

  @Override
  public void deleteGoal(String goalId, String username) {
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
    // TODO: Async event to remove from profiles of author and mentors
    log.info("Deleting goal: {}", goalId);
    gr.delete(goal);
  }

  @Override
  public String extendDeadline(String goalId, String username, String newDeadline) {
    var go = gr.findById(goalId);
    if (!go.isPresent()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
      log.error("Goal: {} is already completed", goalId);
      throw new BadRequestException("Goal: " + goalId + " is already completed");
    }
    if (!username.equals(goal.getMentor1()) && !username.equals(goal.getMentor2())) {
      log.error("User: {} is not a mentor of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not a mentor of goal: " + goalId);
    }
    var updatedDealine = Instant.parse(newDeadline);
    if (goal.getDeadline().isAfter(updatedDealine)) {
      log.error("New deadline cannot be before previous deadline");
      throw new BadRequestException("New deadline cannot be previous deadline");
    }
    log.info("Deadline extended for goal: {}", goalId);
    goal.setDeadline(updatedDealine);
    return gr.save(goal).getId();
  }

  @Override
  public String completeGoal(String goalId, String username) {
    var go = gr.findById(goalId);
    if (!go.isPresent()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!username.equals(goal.getMentor1()) && !username.equals(goal.getMentor2())) {
      log.error("User: {} is not a mentor of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not a mentor of goal: " + goalId);
    }
    if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
      log.error("Goal: {} is already completed", goalId);
      throw new BadRequestException("Goal: " + goalId + " is already completed");
    }
    goal.setStatus(GoalStatus.COMPLETED);
    return gr.save(goal).getId();
  }

}
