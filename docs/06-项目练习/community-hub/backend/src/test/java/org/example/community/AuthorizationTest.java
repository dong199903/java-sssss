package org.example.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void z1PostWithoutTokenIs401() throws Exception {
        mockMvc.perform(post("/api/posts/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"x\",\"content\":\"y\",\"communityId\":1,\"latitude\":31.2,\"longitude\":121.4}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void z2AmyCannotEditZhangsanPost() throws Exception {
        String token = login("amy", "demo123");
        mockMvc.perform(put("/api/posts/303")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"改别人的\",\"content\":\"不行\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void z3AmyCannotReadZhangsan2Notice() throws Exception {
        String token = login("amy", "demo123");
        mockMvc.perform(post("/api/notifications/2122368130222538754/read")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void z4DemoCannotListAdminLogs() throws Exception {
        String token = login("demo", "demo123");
        mockMvc.perform(get("/api/admin/op-logs")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void z5DemoCannotCancelAmysLike() throws Exception {
        String token = login("demo", "demo123");
        mockMvc.perform(delete("/api/posts/101/likes")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"passwordHash\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("data").asText();
    }
}
