package io.github.iamzaidsheikh.sprint.profile.service;

import io.github.iamzaidsheikh.sprint.auth.model.User;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;

public interface IUserProfileService {
    UserProfile createProfile(User user);

    UserProfile getProfile(String username);

    String addGoal(Goal goal, String username);

    String addMentored(Goal goal, String username);

    String removeGoal(String goalId, String username);

}
