package io.github.iamzaidsheikh.sprint.api;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.iamzaidsheikh.sprint.goal.dto.GoalDTO;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.service.IGoalService;
import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;
import io.github.iamzaidsheikh.sprint.task.service.ITaskService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SprintAPI {

  private final IGoalService gs;
  private final ITaskService ts;

  @GetMapping("/goals")
  public ResponseEntity<List<Goal>> getAllGoals() {
    return ResponseEntity.ok().body(gs.getAllGoals());
  }

  @GetMapping("/goals/{goalId}")
  public ResponseEntity<Goal> getGoal(@PathVariable String goalId) {
    return ResponseEntity.ok(gs.getGoal(goalId));
  }

  @PostMapping("/goals")
  public ResponseEntity<String> createGoal(@RequestBody GoalDTO data, HttpServletRequest request,
      Principal principal) {
    var user = principal.getName();
    return ResponseEntity.status(HttpStatus.CREATED).body(gs.createGoal(user, data));
  }

  @GetMapping("/goals/{goalId}/invite")
  public ResponseEntity<String> invite(@PathVariable String goalId, HttpServletRequest request, Principal principal) {
    var user = principal.getName();
    return ResponseEntity.ok(gs.invite(goalId, user));

  }

  @PostMapping("/goals/join/{invCode}")
  public ResponseEntity<String> join(@PathVariable String invCode, HttpServletRequest request, Principal principal) {
    var user = principal.getName();
    return ResponseEntity.status(HttpStatus.CREATED).body(gs.join(invCode, user));

  }

  @PostMapping("/goals/{goalId}/tasks")
  public ResponseEntity<String> createTask(@PathVariable String goalId, @RequestBody TaskDTO data,
      HttpServletRequest request, Principal principal) {
    var user = principal.getName();
    return ResponseEntity.status(HttpStatus.CREATED).body(ts.createTask(goalId, user, data));
  }

  @PostMapping("/goals/{goalId}/tasks/{taskId}/submit")
  public ResponseEntity<String> submitTask(@PathVariable String goalId, @PathVariable String taskId,
      @RequestBody SubmitTaskDTO submission,
      HttpServletRequest request, Principal principal) {
    var user = principal.getName();
    return ResponseEntity.status(HttpStatus.CREATED).body(ts.submitTask(goalId, taskId, user, submission));
  }
}
