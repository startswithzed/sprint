package io.github.iamzaidsheikh.sprint.api;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.iamzaidsheikh.sprint.auth.dto.UserDTO;
import io.github.iamzaidsheikh.sprint.auth.service.IUserService;
import io.github.iamzaidsheikh.sprint.goal.dto.ExtendDTO;
import io.github.iamzaidsheikh.sprint.goal.dto.GoalDTO;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.service.IGoalService;
import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;
import io.github.iamzaidsheikh.sprint.profile.service.IUserProfileService;
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
  private final IUserService us;
  private final IUserProfileService ups;

  @PostMapping("/register")
  public ResponseEntity<UserProfile> registerUser(@RequestBody UserDTO data) {
    var profile = us.registerUser(data);
    return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/profile/{username}")
        .buildAndExpand(profile.getUsername()).toUri()).body(profile);
  }

  @GetMapping("/profile/{username}")
  public ResponseEntity<UserProfile> getProfile(@PathVariable String username) {
    return ResponseEntity.ok(ups.getProfile(username));
  }

  @GetMapping("/goals")
  public ResponseEntity<List<Goal>> getAllGoals() {
    return ResponseEntity.ok().body(gs.getAllGoals());
  }

  @GetMapping("/goals/{goalId}")
  public ResponseEntity<Goal> getGoal(@PathVariable String goalId) {
    return ResponseEntity.ok(gs.getGoal(goalId));
  }

  @PostMapping("/goals")
  public ResponseEntity<String> createGoal(@RequestBody GoalDTO data, Principal principal) {
    var goalId = gs.createGoal(principal.getName(), data);
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(goalId);
  }

  @GetMapping("/goals/{goalId}/invite")
  public ResponseEntity<String> invite(@PathVariable String goalId, Principal principal) {
    return ResponseEntity.ok(gs.invite(goalId, principal.getName()));

  }

  @PutMapping("/goals/join/{invCode}")
  public ResponseEntity<String> join(@PathVariable String invCode, Principal principal) {
    var goalId = gs.join(invCode, principal.getName());
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(goalId);

  }

  @PutMapping("/goals/{goalId}/leave")
  public ResponseEntity<String> leaveGoal(@PathVariable String goalId, Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(gs.leaveGoal(goalId, principal.getName()));
  }

  @DeleteMapping("/goals/{goalId}")
  public ResponseEntity<String> deleteGoal(@PathVariable String goalId, Principal principal) {
    gs.deleteGoal(goalId, principal.getName());
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/goals/{goalId}/extend")
  public ResponseEntity<String> extendDeadline(@PathVariable String goalId, @RequestBody ExtendDTO data,
      Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(gs.extendDeadline(goalId, principal.getName(), data.getDeadline()));
  }

  @PutMapping("/goals/{goalId}/complete")
  public ResponseEntity<String> completeGoal(@PathVariable String goalId, Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(gs.completeGoal(goalId, principal.getName()));
  }

  @PutMapping("/goals/{goalId}/tasks")
  public ResponseEntity<String> createTask(@PathVariable String goalId, @RequestBody TaskDTO data,
      Principal principal) {
    var taskId = ts.createTask(goalId, principal.getName(), data);
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(taskId);
  }

  @PutMapping("/goals/{goalId}/tasks/{taskId}/submit")
  public ResponseEntity<String> submitTask(@PathVariable String goalId, @PathVariable String taskId,
      @RequestBody SubmitTaskDTO submission,
      Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(ts.submitTask(goalId, taskId, principal.getName(), submission));
  }

  @GetMapping("/goals/{goalId}/tasks/{taskId}/submission")
  public ResponseEntity<SubmitTaskDTO> getSubmission(@PathVariable String goalId, @PathVariable String taskId,
      Principal principal) {
    return ResponseEntity.ok(ts.getSubmission(goalId, taskId, principal.getName()));
  }

  @PutMapping("/goals/{goalId}/tasks/{taskId}/approve")
  public ResponseEntity<String> approveTask(@PathVariable String goalId, @PathVariable String taskId,
      Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}/tasks/{taskId}")
        .buildAndExpand(goalId, taskId)
        .toUri();
    return ResponseEntity.created(uri).body(ts.approveTask(goalId, taskId, principal.getName()));
  }

  @DeleteMapping("/goals/{goalId}/tasks/{taskId}")
  public ResponseEntity<String> deleteTask(@PathVariable String goalId, @PathVariable String taskId,
      Principal principal) {
    ts.deleteTask(goalId, taskId, principal.getName());
    return ResponseEntity.noContent().build();
  }
}
