package io.github.iamzaidsheikh.sprint.task.service;

import org.springframework.stereotype.Service;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.repo.GoalRepo;
import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;
import io.github.iamzaidsheikh.sprint.task.model.Task;
import io.github.iamzaidsheikh.sprint.task.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskService implements ITaskService {

  private final GoalRepo gr;

  @Override
  public String createTask(String goalId, String username, TaskDTO data) {
    var go = gr.findById(goalId);
    if (!go.isPresent()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!username.equals(goal.getAuthor()) && !username.equals(goal.getMentor1())
        && !username.equals(goal.getMentor2())) {
      log.error("User: {} is not the author or mentor of the goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not the author or mentor of the goal: " + goalId);
    }
    if (data.getDeadline().isAfter(goal.getDeadline())) {
      log.error("Cannot create a task after goal deadline");
      throw new BadRequestException("Cannot create a task after goal deadline");
    }

    var tasks = goal.getTasks();

    var task = new Task(
        data.getDesc(),
        data.getDeadline(),
        username);

    tasks.add(0, task);
    goal.setTasks(tasks);
    gr.save(goal);
    log.info("Added task: {} to goal: {}", task.getId(), goalId);

    return task.getId();
  }

  @Override
  public String submitTask(String goalId, String taskId, String username, SubmitTaskDTO submission) {
    var go = gr.findById(goalId);
    if (go.isEmpty()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!goal.getAuthor().equals(username)) {
      log.error("User: {} is not the author of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not the author of goal: " + goalId);
    }
    var tasks = goal.getTasks();
    var to = tasks.stream().findFirst().filter(t -> t.getId().equals(taskId));
    if (to.isEmpty()) {
      log.error("Could not find task: {}", taskId);
      throw new ResourceNotFoundException("Could not find task: " + taskId);
    }
    var task = to.get();
    var index = tasks.indexOf(task);
    task.setSubmission(submission);
    task.setStatus(TaskStatus.PENDING);
    log.info("Added a new submission to task: {}", taskId);
    tasks.set(index, task);
    goal.setTasks(tasks);
    return gr.save(goal).getId();
  }

  @Override
  public String approveTask(String goalId, String taskId, String username) {
    var go = gr.findById(goalId);
    if (go.isEmpty()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!username.equals(goal.getMentor1()) && !username.equals(goal.getMentor2())) {
      log.error("User: {} is not a mentor of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not a mentor of goal: " + goalId);
    }
    var tasks = goal.getTasks();
    var to = tasks.stream().findFirst().filter(t -> t.getId().equals(taskId));
    if (to.isEmpty()) {
      log.error("Could not find task: {}", taskId);
      throw new ResourceNotFoundException("Could not find task: " + taskId);
    }
    var task = to.get();
    if (!task.getStatus().equals(TaskStatus.PENDING)) {
      log.error("There is no submission for task: {}", taskId);
      throw new ResourceNotFoundException("There is no submission for task: " + taskId);
    }
    task.setStatus(TaskStatus.COMPLETED);
    var index = tasks.indexOf(task);
    log.error("User: {} approved task: {}", username, taskId);
    tasks.set(index, task);
    goal.setTasks(tasks);
    return gr.save(goal).getId();
  }

  @Override
  public SubmitTaskDTO getSubmission(String goalId, String taskId, String username) {
    var go = gr.findById(goalId);
    if (go.isEmpty()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!username.equals(goal.getMentor1()) && !username.equals(goal.getMentor2())
        && !username.equals(goal.getAuthor())) {
      log.error("User: {} is not a mentor or author of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not a mentor or author of goal: " + goalId);
    }
    var tasks = goal.getTasks();
    var to = tasks.stream().findFirst().filter(t -> t.getId().equals(taskId));
    if (to.isEmpty()) {
      log.error("Could not find task: {}", taskId);
      throw new ResourceNotFoundException("Could not find task: " + taskId);
    }
    var task = to.get();
    if (!task.getStatus().equals(TaskStatus.PENDING)) {
      log.error("There is no submission for task: {}", taskId);
      throw new ResourceNotFoundException("There is no submission for task: " + taskId);
    }
    log.info("Fetching submission for task: {}", taskId);
    return task.getSubmission();
  }

  @Override
  public String deleteTask(String goalId, String taskId, String username) {
    var go = gr.findById(goalId);
    if (go.isEmpty()) {
      log.error("Could not find goal: {}", goalId);
      throw new ResourceNotFoundException("Could not find goal: " + goalId);
    }
    var goal = go.get();
    if (!username.equals(goal.getMentor1()) && !username.equals(goal.getMentor2())
        && !username.equals(goal.getAuthor())) {
      log.error("User: {} is not a mentor or author of goal: {}", username, goalId);
      throw new BadRequestException("User: " + username + " is not a mentor or author of goal: " + goalId);
    }
    var tasks = goal.getTasks();
    var to = tasks.stream().findFirst().filter(t -> t.getId().equals(taskId));
    if (to.isEmpty()) {
      log.error("Could not find task: {}", taskId);
      throw new ResourceNotFoundException("Could not find task: " + taskId);
    }
    var task = to.get();
    var index = tasks.indexOf(task);
    tasks.remove(index);
    goal.setTasks(tasks);
    log.info("User: {} removed task: {}", username, taskId);
    return gr.save(goal).getId();
  }

}
