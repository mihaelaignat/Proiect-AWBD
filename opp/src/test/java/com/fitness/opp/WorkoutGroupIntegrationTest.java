package com.fitness.opp;

import com.fitness.opp.controller.WorkoutGroupController;
import com.fitness.opp.models.WorkoutGroup;
import com.fitness.opp.repository.WorkoutGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class WorkoutGroupIntegrationTest {

    private MockMvc standaloneMockMvc;

    @Autowired
    private WorkoutGroupController workoutGroupController;

    @Autowired
    private WorkoutGroupRepository workoutGroupRepository;

    @BeforeEach
    void setup() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        standaloneMockMvc = MockMvcBuilders.standaloneSetup(workoutGroupController)
                .setViewResolvers(viewResolver)
                .build();

        workoutGroupRepository.deleteAll();
    }

    @Test
    void testListGroups() throws Exception {
        WorkoutGroup group = new WorkoutGroup();
        group.setName("Full Body");
        group.setCategory("Strength");
        workoutGroupRepository.save(group);

        standaloneMockMvc.perform(get("/workout-groups/list")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("groups"))
                .andExpect(view().name("workout-groups-list"));
    }

    @Test
    void testShowFormForAdd() throws Exception {
        standaloneMockMvc.perform(get("/workout-groups/showFormForAdd"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("group"))
                .andExpect(view().name("add-workout-group"));
    }

    @Test
    void testSaveGroup() throws Exception {
        standaloneMockMvc.perform(post("/workout-groups/save")
                        .param("name", "Cardio Blast")
                        .param("description", "High intensity")
                        .param("category", "Cardio"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout-groups/list"));

        assertEquals(1, workoutGroupRepository.findAll().size());
    }

    @Test
    void testShowFormForUpdate() throws Exception {
        WorkoutGroup group = new WorkoutGroup();
        group.setName("Old Name");
        group = workoutGroupRepository.save(group);

        standaloneMockMvc.perform(get("/workout-groups/showFormForUpdate")
                        .param("groupId", group.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("group"))
                .andExpect(view().name("add-workout-group"));
    }

    @Test
    void testDeleteGroup() throws Exception {
        WorkoutGroup group = new WorkoutGroup();
        group.setName("To Delete");
        group = workoutGroupRepository.save(group);

        standaloneMockMvc.perform(get("/workout-groups/delete")
                        .param("groupId", group.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout-groups/list"));

        assertFalse(workoutGroupRepository.findById(group.getId()).isPresent());
    }
}