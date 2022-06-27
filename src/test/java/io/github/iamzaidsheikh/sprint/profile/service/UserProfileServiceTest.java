package io.github.iamzaidsheikh.sprint.profile.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.model.GoalShort;
import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;
import io.github.iamzaidsheikh.sprint.profile.repo.UserProfileRepo;

public class UserProfileServiceTest {

    private UserProfileService underTest;
    @Mock
    private UserProfileRepo upr;
    private AutoCloseable ac;

    @BeforeEach
    void setUp() {
        ac = MockitoAnnotations.openMocks(this);
        underTest = new UserProfileService(upr);
    }

    @AfterEach
    void tearDown() throws Exception {
        ac.close();
    }

    @Test
    void testCannotFindUserProfile() {
        // given
        var user = "testUser";
        var goal = new Goal(user, Instant.now(), "Test Title", "Test Desc");
        // when
        // then
        AssertionsForClassTypes.assertThatThrownBy(() -> underTest.addGoal(goal, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Could not find profile for user: " + user);
    }

    @Test
    void testCanAddGoal() {
        // given
        var user = "testUser";
        var id = "testId";
        var goalId = "goalId";
        var goal = new Goal(user, Instant.now(), "Test Title", "Test Desc");
        goal.setId(goalId);
        var profile = new UserProfile(id, "test", "user", user);
        // when
        when(upr.findByUsername(user)).thenReturn(Optional.of(profile));
        when(upr.save(any(UserProfile.class))).thenReturn(profile);
        // then
        underTest.addGoal(goal, user);
        ArgumentCaptor<UserProfile> upac = ArgumentCaptor.forClass(UserProfile.class);
        verify(upr).save(upac.capture());
        var capturedProfile = upac.getValue();
        AssertionsForClassTypes.assertThat(capturedProfile.getGoals().get(0).getId()).isEqualTo(goalId);
    }

    @Test
    void testCanRemoveGoal() {
        // given
        var user = "testUser";
        var id = "testId";
        var goalId = "goalId";
        var goal = new Goal(user, Instant.now(), "Test Title", "Test Desc");
        goal.setId(goalId);
        var profile = new UserProfile(id, "test", "user", user);
        var goals = new ArrayList<GoalShort>();
        goals.add(new GoalShort(goalId, "Test Title", "Test Desc", user));
        profile.setGoals(goals);
        // when
        when(upr.findByUsername(user)).thenReturn(Optional.of(profile));
        when(upr.save(any(UserProfile.class))).thenReturn(profile);
        // then
        underTest.removeGoal(goalId, user);
        ArgumentCaptor<UserProfile> upac = ArgumentCaptor.forClass(UserProfile.class);
        verify(upr).save(upac.capture());
        var capturedProfile = upac.getValue();
        AssertionsForClassTypes.assertThat(capturedProfile.getGoals().size()).isZero();
    }

    @Test
    void testCanAddMentored() {
        // given
        var user = "testUser";
        var id = "testId";
        var goalId = "goalId";
        var goal = new Goal(user, Instant.now(), "Test Title", "Test Desc");
        goal.setId(goalId);
        var profile = new UserProfile(id, "test", "user", user);
        // when
        when(upr.findByUsername(user)).thenReturn(Optional.of(profile));
        when(upr.save(any(UserProfile.class))).thenReturn(profile);
        // then
        underTest.addMentored(goal, user);
        ArgumentCaptor<UserProfile> upac = ArgumentCaptor.forClass(UserProfile.class);
        verify(upr).save(upac.capture());
        var capturedProfile = upac.getValue();
        AssertionsForClassTypes.assertThat(capturedProfile.getMentored().size()).isNotZero();
    }
}
