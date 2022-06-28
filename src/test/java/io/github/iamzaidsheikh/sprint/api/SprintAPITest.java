package io.github.iamzaidsheikh.sprint.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AutoConfigureMockMvc
@SpringBootTest
public class SprintAPITest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testGetGoalsShouldReturnBadRequest() throws Exception {
    mockMvc.perform(
      MockMvcRequestBuilders.get("/api/v1/goals")
    ).andExpect(MockMvcResultMatchers.status().isBadRequest())
    .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid authorization header"));
  }

  @WithMockUser
  @Test
  void testGetGoalsShouldReturnForbidden() throws Exception {
    mockMvc.perform(
      MockMvcRequestBuilders.get("/api/v1/goals")
      .header("Authorization", "Bearer TestToken")
    ).andExpect(MockMvcResultMatchers.status().isForbidden());
  }

}
