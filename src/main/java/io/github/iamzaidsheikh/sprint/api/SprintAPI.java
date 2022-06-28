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
import io.github.iamzaidsheikh.sprint.exception.Error;
import io.github.iamzaidsheikh.sprint.goal.dto.ExtendDTO;
import io.github.iamzaidsheikh.sprint.goal.dto.GoalDTO;
import io.github.iamzaidsheikh.sprint.goal.model.Goal;
import io.github.iamzaidsheikh.sprint.goal.service.IGoalService;
import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;
import io.github.iamzaidsheikh.sprint.profile.service.IUserProfileService;
import io.github.iamzaidsheikh.sprint.task.dto.SubmitTaskDTO;
import io.github.iamzaidsheikh.sprint.task.dto.TaskDTO;
import io.github.iamzaidsheikh.sprint.task.service.ITaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SprintAPI {

  private final IGoalService gs;
  private final ITaskService ts;
  private final IUserService us;
  private final IUserProfileService ups;

  @Operation(summary = "Register new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Registered a new user", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class)) }),
      @ApiResponse(responseCode = "409", description = "Username already exists", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "404", content = {
          @Content(mediaType = "application/json") })
  })
  @PostMapping("/register")
  public ResponseEntity<UserProfile> registerUser(@RequestBody UserDTO data) {
    var profile = us.registerUser(data);
    return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/profile/{username}")
        .buildAndExpand(profile.getUsername()).toUri()).body(profile);
  }

  @Operation(summary = "Get user profile")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Fetched user profile", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class)) }),
      @ApiResponse(responseCode = "404", description = "Profile not found", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) })
  })
  @GetMapping("/profile/{username}")
  public ResponseEntity<UserProfile> getProfile(@PathVariable String username) {
    return ResponseEntity.ok(ups.getProfile(username));
  }

  @Operation(summary = "Get all goals")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Fetched all goals", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Goal.class)) }),
      @ApiResponse(responseCode = "400", description = "Invalid authorization header", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "403", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @GetMapping("/goals")
  public ResponseEntity<List<Goal>> getAllGoals() {
    return ResponseEntity.ok().body(gs.getAllGoals());
  }

  @Operation(summary = "Fetch goal by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Fetched goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Goal.class)) }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @GetMapping("/goals/{goalId}")
  public ResponseEntity<Goal> getGoal(@PathVariable String goalId) {
    return ResponseEntity.ok(gs.getGoal(goalId));
  }

  @Operation(summary = "Create a new goal")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created a new goal", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400")
  })
  @PostMapping("/goals")
  public ResponseEntity<String> createGoal(@RequestBody GoalDTO data, Principal principal) {
    var goalId = gs.createGoal(principal.getName(), data);
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(goalId);
  }

  @Operation(summary = "Invite a mentor for a goal")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Invite code generated", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @GetMapping("/goals/{goalId}/invite")
  public ResponseEntity<String> invite(@PathVariable String goalId, Principal principal) {
    return ResponseEntity.ok(gs.invite(goalId, principal.getName()));

  }

  @Operation(summary = "Join goal as mentor using invite code")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Joined goal as a mentor", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/join/{invCode}")
  public ResponseEntity<String> join(@PathVariable String invCode, Principal principal) {
    var goalId = gs.join(invCode, principal.getName());
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(goalId);

  }

  @Operation(summary = "Leave mentorship of a goal")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Left mentorship of goal", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/{goalId}/leave")
  public ResponseEntity<String> leaveGoal(@PathVariable String goalId, Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(gs.leaveGoal(goalId, principal.getName()));
  }

  @Operation(summary = "Delete goal by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Goal deleted"),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @DeleteMapping("/goals/{goalId}")
  public ResponseEntity<String> deleteGoal(@PathVariable String goalId, Principal principal) {
    gs.deleteGoal(goalId, principal.getName());
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Extend goal deadline")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Deadline extended", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/{goalId}/extend")
  public ResponseEntity<String> extendDeadline(@PathVariable String goalId, @RequestBody ExtendDTO data,
      Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(gs.extendDeadline(goalId, principal.getName(), data.getDeadline()));
  }

  @Operation(summary = "Complete goal")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Goal completed", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/{goalId}/complete")
  public ResponseEntity<String> completeGoal(@PathVariable String goalId, Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(gs.completeGoal(goalId, principal.getName()));
  }

  @Operation(summary = "Create a new task for goal")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Task created", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/{goalId}/tasks")
  public ResponseEntity<String> createTask(@PathVariable String goalId, @RequestBody TaskDTO data,
      Principal principal) {
    var taskId = ts.createTask(goalId, principal.getName(), data);
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(taskId);
  }

  @Operation(summary = "Submit a task")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Task submitted", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "404", description = "Could not find task", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/{goalId}/tasks/{taskId}/submit")
  public ResponseEntity<String> submitTask(@PathVariable String goalId, @PathVariable String taskId,
      @RequestBody SubmitTaskDTO submission,
      Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}").buildAndExpand(goalId)
        .toUri();
    return ResponseEntity.created(uri).body(ts.submitTask(goalId, taskId, principal.getName(), submission));
  }

  @Operation(summary = "Fetch submission by taskId")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Fetched submission", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = SubmitTaskDTO.class)) }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "404", description = "Could not find task", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @GetMapping("/goals/{goalId}/tasks/{taskId}/submission")
  public ResponseEntity<SubmitTaskDTO> getSubmission(@PathVariable String goalId, @PathVariable String taskId,
      Principal principal) {
    return ResponseEntity.ok(ts.getSubmission(goalId, taskId, principal.getName()));
  }

  @Operation(summary = "Approve task submission")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Submission approved", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "404", description = "Could not find task", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "404", description = "No submission found", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @PutMapping("/goals/{goalId}/tasks/{taskId}/approve")
  public ResponseEntity<String> approveTask(@PathVariable String goalId, @PathVariable String taskId,
      Principal principal) {
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/goals/{goalId}/tasks/{taskId}")
        .buildAndExpand(goalId, taskId)
        .toUri();
    return ResponseEntity.created(uri).body(ts.approveTask(goalId, taskId, principal.getName()));
  }

  @Operation(summary = "Delete task by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Task deleted", content = {
          @Content(mediaType = "text/plain") }),
      @ApiResponse(responseCode = "404", description = "Could not find goal", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "404", description = "Could not find task", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)) }),
      @ApiResponse(responseCode = "401", content = {
          @Content(mediaType = "application/json") }),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json") })
  })
  @Parameter(in = ParameterIn.HEADER, required = true, name = "Authorization", description = "Authorization token")
  @DeleteMapping("/goals/{goalId}/tasks/{taskId}")
  public ResponseEntity<String> deleteTask(@PathVariable String goalId, @PathVariable String taskId,
      Principal principal) {
    ts.deleteTask(goalId, taskId, principal.getName());
    return ResponseEntity.noContent().build();
  }
}
