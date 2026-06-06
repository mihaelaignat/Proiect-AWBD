package com.fitness.opp;

import com.fitness.opp.models.Coach;
import com.fitness.opp.repository.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class CoachIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CoachRepository coachRepository;

    @BeforeEach
    void setup() {
        coachRepository.deleteAll();
    }

    @Test
    void testListCoaches() throws Exception {
        mockMvc.perform(get("/coaches/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("coaches-list"));
    }

    @Test
    void testSaveCoach() throws Exception {
        mockMvc.perform(post("/coaches/save")
                        .with(csrf())
                        .param("name", "Antrenor Test")
                        .param("specialization", "Fitness"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coaches/list"));

        assertEquals(1, coachRepository.findAll().size());
    }

    @Test
    void testDeleteCoach() throws Exception {
        Coach coach = new Coach();
        coach.setName("De Sters");
        coach.setSpecialization("Yoga");
        coach = coachRepository.save(coach);

        mockMvc.perform(get("/coaches/delete")
                        .param("coachId", coach.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coaches/list"));

        assertFalse(coachRepository.findById(coach.getId()).isPresent());
    }
}