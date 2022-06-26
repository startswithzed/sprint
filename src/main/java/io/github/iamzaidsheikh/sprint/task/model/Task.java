package io.github.iamzaidsheikh.sprint.task.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Field;

import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import lombok.Data;

@Data
public class Task {

  private String id;

  private String desc;

  @Field(name = "assigned_by")
  private String assignedBy;

  private LocalDateTime deadline;

  @Field(name = "created_at")
  private LocalDateTime createdAt;

  private TaskStatus status;

  private SubmitTaskDTO submission;

  public Task(String desc, LocalDateTime deadline, String assignedBy) {
    this.id = UUID.randomUUID().toString();
    this.desc = desc;
    this.assignedBy = assignedBy;
    this.deadline = deadline;
    this.createdAt = LocalDateTime.now();
    this.status = TaskStatus.NEW;
    this.submission = null;
  }

}
