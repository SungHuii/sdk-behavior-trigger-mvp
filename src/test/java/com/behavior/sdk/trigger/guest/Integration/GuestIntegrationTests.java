package com.behavior.sdk.trigger.guest.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import redis.embedded.RedisServer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GuestIntegrationTests {

   static RedisServer redisServer;

   @BeforeAll
   static void beforeAll() throws IOException {
      redisServer = RedisServer.builder()
                  .port(6379).build();
      redisServer.start();
   }

   @AfterAll
   static void afterAll() {
      redisServer.stop();
   }

   @Autowired
   MockMvc mockMvc;
   @Autowired
   ObjectMapper om;

   @Test
   void createGetDeleteGuest() throws Exception {

      // 생성
      String json = mockMvc.perform(post("/api/guests")
               .param("name", "홍성휘")
               .param("email", "sunghui@email.com"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.guestId").exists())
            .andReturn().getResponse().getContentAsString();

      UUID guestId = UUID.fromString(om.readTree(json).get("guestId").asText());

      // 조회
      mockMvc.perform(get("/api/guests/{id}", guestId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("홍성휘"));

      // 삭제
      mockMvc.perform(delete("/api/guests/{id}", guestId))
            .andExpect(status().isNoContent());
   }
}
