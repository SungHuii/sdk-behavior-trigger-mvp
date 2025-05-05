package com.behavior.sdk.trigger.visitor.controller;

import com.behavior.sdk.trigger.visitor.service.VisitorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitorController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VisitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitorService visitorService;

    @Test
    void existsReturnsTrueWhenVisitorExists() throws Exception {

        UUID visitorId = UUID.randomUUID();
        given(visitorService.existsVisitor(visitorId)).willReturn(true);

        mockMvc.perform(get("/api/visitors/exists/{visitorId}", visitorId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existsReturnsFalseWhenVisitorDoesNotExist() throws Exception {

        UUID visitorId = UUID.randomUUID();
        given(visitorService.existsVisitor(visitorId)).willReturn(false);

        mockMvc.perform(get("/api/visitors/exists/{visitorId}", visitorId))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}