package io.github.iamzaidsheikh.sprint.goal.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.model.GoalStatus;
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
    var testGoal = new Goal("test author", Instant.now(), "test title", "test desc");
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
    var testGoal = new Goal("wrongUsername", Instant.now(), "test title", "test desc");
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
    var testGoal = new Goal(username, Instant.now(), "test title", "test desc");
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
    var goal = new Goal(username, Instant.now(), "Test Title", "Test Desc");
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
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
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
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
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
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId("testId");
    // when
    when(gr.findByInvCode(invCode)).thenReturn(List.of(goal));
    when(gr.save(any(Goal.class))).thenReturn(goal);
    // then
    underTest.join(invCode, "testUser");
    verify(gr).save(any(Goal.class));
  }

  @Test
  void cannotLeaveGoalAsItIsAlreadyCompleted() {
    // given
    var id = "testId";
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    goal.setStatus(GoalStatus.COMPLETED);
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.leaveGoal(id, "testUser"))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Goal: " + id + " is already completed");
  }

  @Test
  void testCannotLeaveGoalAsUserIsNotAMentor() {
    // given
    var id = "testId";
    var user = "testUser";
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    goal.setMentor1("testMentor1");
    goal.setMentor2("testMentor2");
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.leaveGoal(id, user))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User: " + user + " is not a mentor of goal: " + id);
  }

  @Test
  void testCanLeaveGoal() {
    // given
    var id = "testId";
    var user = "testUser";
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    goal.setMentor1(user);
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    when(gr.save(any(Goal.class))).thenReturn(goal);
    // then
    underTest.leaveGoal(id, user);
    ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
    verify(gr).save(gac.capture());
    var capturedGoal = gac.getValue();
    AssertionsForClassTypes.assertThat(capturedGoal.getMentor1()).isNull();
  }

  @Test
  void testCanDeleteGoal() {
    // given
    var id = "testId";
    var user = "testUser";
    var goal = new Goal(user, Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    // then
    underTest.deleteGoal(id, user);
    verify(gr).delete(any(Goal.class));
  }

  @Test
  void testCannotExtendDeadlineBecauseNewDeadlineIsBeforeDeadline() {
    // given
    var id = "testId";
    var user = "testUser";
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    goal.setMentor1(user);
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    // then
    AssertionsForClassTypes
        .assertThatThrownBy(() -> underTest.extendDeadline(id, user, Instant.now().minusSeconds(60 * 60).toString()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("New deadline cannot be previous deadline");
  }

  @Test
  void testCanExtendDeadline() {
    // given
    var id = "testId";
    var user = "testUser";
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    goal.setMentor1(user);
    var deadline = Instant.now().plusSeconds(60 * 60 * 24);
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    when(gr.save(any(Goal.class))).thenReturn(goal);
    // then
    underTest.extendDeadline(id, user, deadline.toString());
    ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
    verify(gr).save(gac.capture());
    var capturedGoal = gac.getValue();
    AssertionsForClassTypes.assertThat(capturedGoal.getDeadline()).isEqualTo(deadline);
  }

  @Test
  void testCanCompleteGoal() {
    // given
    var id = "testId";
    var user = "testUser";
    var goal = new Goal("testAuthor", Instant.now(), "Test Title", "Test Desc");
    goal.setId(id);
    goal.setMentor1(user);
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    when(gr.save(any(Goal.class))).thenReturn(goal);
    // then
    underTest.completeGoal(id, user);
    ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
    verify(gr).save(gac.capture());
    var capturedGoal = gac.getValue();
    AssertionsForClassTypes.assertThat(capturedGoal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
  }
}
