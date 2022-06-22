package io.github.iamzaidsheikh.sprint.task.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.repo.GoalRepo;
import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;

public class TaskServiceTest {

  private TaskService underTest;
  @Mock
  private GoalRepo gr;
  private AutoCloseable ac;

  @BeforeEach
  void setUp() {
    ac = MockitoAnnotations.openMocks(this);
    underTest = new TaskService(gr);
  }

  @AfterEach
  void tearDown() throws Exception {
    ac.close();
  }

  @Test
  void testCanCreateTask() {
    // given
    var id = "id";
    var user = "testUser";
    var goal = new Goal(
        user, LocalDateTime.now(),
        "Test Title",
        "Test Desc");
    var data = new TaskDTO("Test Task Desc", LocalDateTime.now().minusHours(5));
    // when
    when(gr.findById(anyString())).thenReturn(Optional.of(goal));
    underTest.createTask(id, user, data);
    // then
    ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
    verify(gr).save(gac.capture());
    var capturedGoal = gac.getValue();
    AssertionsForClassTypes.assertThat(capturedGoal).satisfies(g -> {
      g.getTasks().get(0).getDesc().equals(data.getDesc());
    });
  }

  @Test
  void testCouldNotCreateTaskBecauseUserIsNotAuthorOrMentor() {
    // given
    var id = "id";
    var goal = new Goal(
        "testAuthor", LocalDateTime.now(),
        "Test Title",
        "Test Desc");
    goal.setMentor1("mentor1");
    goal.setMentor2("mentor2");
    var data = new TaskDTO("Test Task Desc", LocalDateTime.now().minusDays(1));
    var user = "testUser";
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.createTask(id, user, data))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User: " + user + " is not the author or mentor of the goal: " + id);
  }

  @Test
  void testCouldNotCreateTaskBecauseTaskIsAfterGoalDeadline() {
    // given
    var id = "id";
    var user = "testUser";
    var goal = new Goal(
        user, LocalDateTime.now(),
        "Test Title",
        "Test Desc");
    var data = new TaskDTO("Test Task Desc", LocalDateTime.now().plusSeconds(1));
    // when
    when(gr.findById(id)).thenReturn(Optional.of(goal));
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.createTask(id, user, data))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Cannot create a task after goal deadline");
  }
}
