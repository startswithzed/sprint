package io.github.iamzaidsheikh.sprint.goal.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.repo.GoalRepo;

public class GoalServiceTest {

  private GoalService underTest;
  @Mock
  private GoalRepo gr;
  private AutoCloseable ac;

  @BeforeEach
  void setUp() {
    ac = MockitoAnnotations.openMocks(this);
    underTest = new GoalService(gr);
  }

  @AfterEach
  void tearDown() throws Exception {
    ac.close();
  }

  @Test
  void testShouldReturnAllGoals() {
    // when
    underTest.getAllGoals();
    // then
    verify(gr).findAll();
  }

  @Test
  void testShouldReturnGoalById() {
    // given
    var testGoal = new Goal("test author", LocalDateTime.now(), "test title", "test desc");
    var id = "testId";
    testGoal.setId(id);
    // when
    Mockito.when(gr.findById(id)).thenReturn(Optional.of(testGoal));
    // then
    var goal = underTest.getGoal(id);
    assertSame(testGoal, goal);
  }

  @Test
  void testShouldNotFindGoal() {
    // given
    var id = "id";
    // when
    when(gr.findById(id)).thenReturn(Optional.empty());
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.getGoal(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Could not find goal: " + id);
  }

  @Test
  void testCannotInviteBecauseUserNotAuthor() {
    // given
    var id = "testId";
    var username = "testUser";
    var testGoal = new Goal("wrongUsername", LocalDateTime.now(), "test title", "test desc");
    testGoal.setId(id);
    // when
    Mockito.when(gr.findById(id)).thenReturn(Optional.of(testGoal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.invite(id, username))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User: " + username + " is not the author of goal: " + id);
  }

  @Test
  void testCanInvite() {
    // given
    var id = "testId";
    var username = "testUser";
    var testGoal = new Goal(username, LocalDateTime.now(), "test title", "test desc");
    testGoal.setId(id);
    // when
    Mockito.when(gr.findById(id)).thenReturn(Optional.of(testGoal));
    when(gr.save(any(Goal.class))).thenReturn(testGoal);
    underTest.invite(id, username);
    // then
    verify(gr).save(any(Goal.class));
  }

  @Test
  void testCannotFindGoalByInviteCode() {
    // given
    var invCode = "testCode";
    // when
    when(gr.findByInvCode(invCode)).thenReturn(List.of());
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.join(invCode, "testUser"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Could not find goal with invite code: " + invCode);
  }

  @Test
  void testAuthorCannotJoinGoalAsMentor() {
    // given
    var invCode = "testCode";
    var username = "testUser";
    var goal = new Goal(username, LocalDateTime.now(), "Test Title", "Test Desc");
    // when
    when(gr.findByInvCode(invCode)).thenReturn(List.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.join(invCode, username))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Author: " + username + " can't join a goal as a mentor");
  }

  @Test
  void testCannotJoinGoalAsMentorBecauseItAlreadyHasTwoMentors() {
    // given
    var invCode = "testCode";
    var goal = new Goal("testAuthor", LocalDateTime.now(), "Test Title", "Test Desc");
    goal.setId("testId");
    goal.setMentor1("demo1");
    goal.setMentor2("demo2");
    // when
    when(gr.findByInvCode(invCode)).thenReturn(List.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.join(invCode, "testUser"))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Goal: " + goal.getId() + " already has two mentors");
  }

  @Test
  void testCannotJoinGoalAsMentorBecauseUserIsAlreadyAMentor() {
    // given
    var invCode = "testCode";
    var username = "testUser";
    var goal = new Goal("testAuthor", LocalDateTime.now(), "Test Title", "Test Desc");
    goal.setId("testId");
    goal.setMentor1(username);
    // when
    when(gr.findByInvCode(invCode)).thenReturn(List.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.join(invCode, username))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User: " + username + " is already a mentor for goal: " + goal.getId());
  }

  @Test
  void testCanJoinGoalAsAMentor() {
    // given
    var invCode = "testCode";
    var goal = new Goal("testAuthor", LocalDateTime.now(), "Test Title", "Test Desc");
    goal.setId("testId");
    // when
    when(gr.findByInvCode(invCode)).thenReturn(List.of(goal));
    when(gr.save(any(Goal.class))).thenReturn(goal);
    // then
    underTest.join(invCode, "testUser");
    verify(gr).save(any(Goal.class));
  }
}
