package io.github.iamzaidsheikh.sprint.goal.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GoalShort {
    private String id;
    private String title;
    private String desc;
    private String author;
}
