package io.github.iamzaidsheikh.sprint.goal.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
  void testGetAllGoals() {
    // when
    underTest.getAllGoals();
    // then
    verify(gr).findAll();
  }

  @Test
  void testGetGoal() {
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
  void testCouldNotGetGoal() {
    // given
    var id = "id";
    // when
    when(gr.findById(id)).thenReturn(Optional.empty());
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.getGoal(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Could not find goal: " + id);

  }
}
