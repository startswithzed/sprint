package io.github.iamzaidsheikh.sprint.task.service;

import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;

public interface ITaskService {
  String createTask(String goalId, String username, TaskDTO data);

  String submitTask(String goalId, String taskId, String username, SubmitTaskDTO link);

  SubmitTaskDTO getSubmission(String goalId, String taskId, String username);

  String approveTask(String goalId, String taskId, String username);

  String deleteTask(String goalId, String taskId, String username);
}
