package io.github.iamzaidsheikh.sprint.task.service;

import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;

public interface ITaskService {
  String createTask(String goalId, String username, TaskDTO data);
}
