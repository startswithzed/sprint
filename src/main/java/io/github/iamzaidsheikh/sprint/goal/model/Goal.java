package io.github.iamzaidsheikh.sprint.goal.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.github.iamzaidsheikh.sprint.common.BaseEntity;
import io.github.iamzaidsheikh.sprint.task.model.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Document
public class Goal extends BaseEntity {

  @Id
  private String id;

  private String author;

  @Field(name = "mentor_1")
  private String mentor1;

  @Field(name = "mentor_2")
  private String mentor2;

  private Instant deadline;

  private String title;

  private String desc;

  private GoalStatus status;

  @Indexed(unique = true)
  @Field(name = "inv_code")
  private String invCode;

  private List<Task> tasks;

  public Goal(String author, Instant deadline, String title, String desc) {
    this.author = author;
    this.deadline = deadline;
    this.title = title;
    this.desc = desc;
    this.status = GoalStatus.IN_PROGRESS;
    this.mentor1 = null;
    this.mentor2 = null;
    this.invCode = null;
    this.tasks = new ArrayList<>();
  }
}
