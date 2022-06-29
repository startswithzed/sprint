package io.github.iamzaidsheikh.sprint.task.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.github.iamzaidsheikh.sprint.exception.BadRequestException;
import io.github.iamzaidsheikh.sprint.exception.ResourceNotFoundException;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.repo.GoalRepo;
import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;
import io.github.iamzaidsheikh.sprint.task.model.Task;
import io.github.iamzaidsheikh.sprint.task.model.TaskStatus;

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
                user, Instant.now(),
                "Test Title",
                "Test Desc");
        var data = new TaskDTO("Test Task Desc", Instant.now().minusSeconds(60 * 60 * 4).toString());
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
        Assertions.assertThat(capturedGoal.getCreatedAt()).isEqualTo(capturedGoal.getLastModified());
    }

    @Test
    void testCouldNotCreateTaskBecauseUserIsNotAuthorOrMentor() {
        // given
        var id = "id";
        var goal = new Goal(
                "testAuthor", Instant.now(),
                "Test Title",
                "Test Desc");
        goal.setMentor1("mentor1");
        goal.setMentor2("mentor2");
        var data = new TaskDTO("Test Task Desc", Instant.now().minusSeconds(60 * 60 * 24 * 6).toString());
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
                user, Instant.now(),
                "Test Title",
                "Test Desc");
        var data = new TaskDTO("Test Task Desc", Instant.now().plusSeconds(1).toString());
        // when
        when(gr.findById(id)).thenReturn(Optional.of(goal));
        // then
        AssertionsForClassTypes.assertThatThrownBy(() -> underTest.createTask(id, user, data))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot create a task after goal deadline");
    }

    @Test
    void testCouldNotFindTask() {
        // given
        var goalId = "goalId";
        var taskId = "taskId";
        var user = "testUser";
        var goal = new Goal(
                user, Instant.now(),
                "Test Title",
                "Test Desc");
        // when
        when(gr.findById(goalId)).thenReturn(Optional.of(goal));
        // then
        AssertionsForClassTypes
                .assertThatThrownBy(
                        () -> underTest.submitTask(goalId, taskId, user, new SubmitTaskDTO("comment", "link")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Could not find task: " + taskId);
    }

    @Test
    void testCanSubmitTask() {
        // given
        var goalId = "goalId";
        var taskId = "taskId";
        var user = "testUser";
        var goal = new Goal(
                user, Instant.now(),
                "Test Title",
                "Test Desc");
        goal.setId(goalId);
        var task = new Task("Test Desc", Instant.now(), "testAssignee");
        var tasks = new ArrayList<Task>();
        tasks.add(task);
        task.setId(taskId);
        var submission = new SubmitTaskDTO("comment", "link");
        goal.setTasks(tasks);
        // when
        when(gr.findById(goalId)).thenReturn(Optional.of(goal));
        when(gr.save(any(Goal.class))).thenReturn(goal);
        // then
        underTest.submitTask(goalId, taskId, user, submission);
        ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
        verify(gr).save(gac.capture());
        var capturedGoal = gac.getValue();
        var capturedTask = capturedGoal.getTasks().stream().findFirst().filter(t -> t.getId().matches(taskId)).get();
        AssertionsForClassTypes
                .assertThat(
                        capturedTask.getSubmission())
                .isEqualTo(submission);
        AssertionsForClassTypes
                .assertThat(
                        capturedTask.getStatus())
                .isEqualTo(TaskStatus.PENDING);

    }

    @Test
    void testCannotApproveTaskBecauseStatusIsNotPending() {
        // given
        var goalId = "goalId";
        var taskId = "taskId";
        var user = "testUser";
        var goal = new Goal(
                "testAuthor", Instant.now(),
                "Test Title",
                "Test Desc");
        goal.setId(goalId);
        goal.setMentor1(user);
        var task = new Task("Test Desc", Instant.now(), "testAssignee");
        var tasks = new ArrayList<Task>();
        tasks.add(task);
        task.setId(taskId);
        goal.setTasks(tasks);
        // when
        when(gr.findById(goalId)).thenReturn(Optional.of(goal));
        // then
        AssertionsForClassTypes.assertThatThrownBy(() -> underTest.approveTask(goalId, taskId, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("There is no submission for task: " + taskId);
    }

    @Test
    void testCanApproveTask() {
        // given
        var goalId = "goalId";
        var taskId = "taskId";
        var user = "testUser";
        var goal = new Goal(
                "testAuthor", Instant.now(),
                "Test Title",
                "Test Desc");
        goal.setId(goalId);
        goal.setMentor1(user);
        var task = new Task("Test Desc", Instant.now(), "testAssignee");
        task.setStatus(TaskStatus.PENDING);
        var tasks = new ArrayList<Task>();
        tasks.add(task);
        task.setId(taskId);
        goal.setTasks(tasks);
        // when
        when(gr.findById(goalId)).thenReturn(Optional.of(goal));
        when(gr.save(any(Goal.class))).thenReturn(goal);
        // then
        underTest.approveTask(goalId, taskId, user);
        ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
        verify(gr).save(gac.capture());
        var capturedGoal = gac.getValue();
        var capturedTask = capturedGoal.getTasks().stream().findFirst().filter(t -> t.getId().matches(taskId)).get();
        AssertionsForClassTypes
                .assertThat(
                        capturedTask.getStatus())
                .isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    void testCannotGetSubmissionBecauseThereIsNoSubmission() {
        // given
        var goalId = "goalId";
        var taskId = "taskId";
        var user = "testUser";
        var goal = new Goal(
                "testAuthor", Instant.now(),
                "Test Title",
                "Test Desc");
        goal.setId(goalId);
        goal.setMentor1(user);
        var task = new Task("Test Desc", Instant.now(), "testAssignee");
        var testSubmission = new SubmitTaskDTO("comment", "link");
        task.setStatus(TaskStatus.PENDING);
        task.setSubmission(testSubmission);
        var tasks = new ArrayList<Task>();
        tasks.add(task);
        task.setId(taskId);
        goal.setTasks(tasks);
        // when
        when(gr.findById(goalId)).thenReturn(Optional.of(goal));
        // then
        var submission = underTest.getSubmission(goalId, taskId, user);
        AssertionsForClassTypes
                .assertThat(
                        testSubmission)
                .isEqualTo(submission);
    }

    @Test
    void testCanDeleteTask() {
        // given
        var goalId = "goalId";
        var taskId = "taskId";
        var user = "testUser";
        var goal = new Goal(
                "testAuthor", Instant.now(),
                "Test Title",
                "Test Desc");
        goal.setId(goalId);
        goal.setMentor1(user);
        var task = new Task("Test Desc", Instant.now(), "testAssignee");
        task.setId(taskId);
        var tasks = new ArrayList<Task>();
        tasks.add(task);
        goal.setTasks(tasks);
        // when
        when(gr.findById(goalId)).thenReturn(Optional.of(goal));
        when(gr.save(any(Goal.class))).thenReturn(goal);
        // then
        underTest.deleteTask(goalId, taskId, user);
        ArgumentCaptor<Goal> gac = ArgumentCaptor.forClass(Goal.class);
        verify(gr).save(gac.capture());
        var capturedGoal = gac.getValue();
        AssertionsForClassTypes
                .assertThat(
                        capturedGoal.getTasks().size())
                .isZero();

    }
}
