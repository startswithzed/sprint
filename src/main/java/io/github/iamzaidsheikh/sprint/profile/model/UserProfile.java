package io.github.iamzaidsheikh.sprint.profile.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import io.github.iamzaidsheikh.sprint.common.BaseEntity;
import io.github.iamzaidsheikh.sprint.goal.model.GoalShort;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Document
public class UserProfile extends BaseEntity {
    @Id
    private String id;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Indexed(unique = true)
    private String username;

    @Field(name = "num_goals_completed")
    private long numGoalsCompleted;

    @Field(name = "num_goals_mentored")
    private long numGoalsMentored;

    private List<GoalShort> goals;

    private List<GoalShort> mentored;

    public UserProfile(String id, String firstName, String lastName, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.numGoalsCompleted = 0;
        this.numGoalsMentored = 0;
        this.goals = new ArrayList<>();
        this.mentored = new ArrayList<>();
    }

}
