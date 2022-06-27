package io.github.iamzaidsheikh.sprint.task.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Field;

import io.github.iamzaidsheikh.sprint.common.BaseEntity;
import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Task extends BaseEntity {

  private String id;

  private String desc;

  @Field(name = "assigned_by")
  private String assignedBy;

  private Instant deadline;

  private TaskStatus status;

  private SubmitTaskDTO submission;

  public Task(String desc, Instant deadline, String assignedBy) {
    this.id = UUID.randomUUID().toString();
    this.desc = desc;
    this.assignedBy = assignedBy;
    this.deadline = deadline;
    this.status = TaskStatus.NEW;
    this.submission = null;
  }

}
