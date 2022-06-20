package io.github.iamzaidsheikh.sprint.goal.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.github.iamzaidsheikh.sprint.task.model.Task;
import lombok.Data;

@Data
@Document
public class Goal {

  @Id
  private String id;

  private String author;

  @Field(name = "mentor_1")
  private String mentor1;

  @Field(name = "mentor_2")
  private String mentor2;

  private LocalDateTime deadline;

  private String title;

  private String desc;

  @Indexed(unique = true)
  @Field(name = "inv_code")
  private String invCode;

  @Field(name = "created_at")
  private LocalDateTime createdAt;

  private List<Task> tasks;

  public Goal(String author, LocalDateTime deadline, String title, String desc) {
    this.author = author;
    this.deadline = deadline;
    this.title = title;
    this.desc = desc;
    this.mentor1 = null;
    this.mentor2 = null;
    this.invCode = null;
    this.tasks = new ArrayList<>();
    this.createdAt = LocalDateTime.now();
  }
}
