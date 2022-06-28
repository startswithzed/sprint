package io.github.iamzaidsheikh.sprint.profile.service;

import org.springframework.stereotype.Service;

import io.github.iamzaidsheikh.sprint.auth.model.User;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.model.GoalShort;
import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;
import io.github.iamzaidsheikh.sprint.profile.repo.UserProfileRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserProfileService implements IUserProfileService {

    private final UserProfileRepo upr;

    @Override
    public UserProfile createProfile(User user) {
        var profile = new UserProfile(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername());
        log.info("Creating profile for user: {}", user.getUsername());
        return upr.save(profile);
    }

    @Override
    public UserProfile getProfile(String username) {
        var profile = upr.findByUsername(username);
        if (profile.isEmpty()) {
            log.error("Could not find profile for user: {}", username);
            throw new ResourceNotFoundException("Could not find profile for user: " + username);
        }
        return profile.get();
    }

    @Override
    public String addGoal(Goal goal, String username) {
        var po = upr.findByUsername(username);
        if (po.isEmpty()) {
            log.error("Could not find profile for user: {}", username);
            throw new ResourceNotFoundException("Could not find profile for user: " + username);
        }
        var profile = po.get();
        var goals = profile.getGoals();
        goals.add(new GoalShort(goal.getId(),
                goal.getTitle(),
                goal.getDesc(),
                goal.getAuthor()));
        log.info("Adding goal: {} to user: {} 's profile", goal.getId(), username);
        profile.setGoals(goals);
        return upr.save(profile).getUsername();
    }

    @Override
    public String removeGoal(String goalId, String username) {
        var po = upr.findByUsername(username);
        if (po.isEmpty()) {
            log.error("Could not find profile for user: {}", username);
            throw new ResourceNotFoundException("Could not find profile for user: " + username);
        }
        var profile = po.get();
        var goals = profile.getGoals();
        var go = goals.stream().findFirst().filter(g -> g.getId().equals(goalId));
        if (go.isEmpty()) {
            log.error("Could not find goal: {}", goalId);
            throw new ResourceNotFoundException("Could not find goal: " + goalId);
        }
        goals.remove(go.get());
        return upr.save(profile).getUsername();
    }

    @Override
    public String addMentored(Goal goal, String username) {
        var po = upr.findByUsername(username);
        if (po.isEmpty()) {
            log.error("Could not find profile for user: {}", username);
            throw new ResourceNotFoundException("Could not find profile for user: " + username);
        }
        var profile = po.get();
        var mentored = profile.getMentored();
        mentored.add(new GoalShort(goal.getId(),
                goal.getTitle(),
                goal.getDesc(),
                goal.getAuthor()));
        log.info("Adding goal: {} to user: {} 's profile", goal.getId(), username);
        profile.setMentored(mentored);
        return upr.save(profile).getUsername();
    }

}
