package com.fitness.opp.controller;

import com.fitness.opp.models.WorkoutGroup;
import com.fitness.opp.repository.ExerciseRepository;
import com.fitness.opp.repository.WorkoutGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkoutGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutGroupRepository workoutGroupRepository;

    @MockBean
    private ExerciseRepository exerciseRepository;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testListGroups() throws Exception {
        when(workoutGroupRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/workout-groups/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("workout-groups-list"))
                .andExpect(model().attributeExists("groups"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowFormForAdd() throws Exception {
        mockMvc.perform(get("/workout-groups/showFormForAdd"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-workout-group"))
                .andExpect(model().attributeExists("group"))
                .andExpect(model().attributeExists("allExercises"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testSaveGroupWithExercises() throws Exception {
        mockMvc.perform(post("/workout-groups/save")
                        .with(csrf())
                        .param("exerciseIds", "1", "2")
                        .flashAttr("group", new WorkoutGroup()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout-groups/list"));

        verify(workoutGroupRepository, times(1)).save(any(WorkoutGroup.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testDeleteGroup() throws Exception {
        mockMvc.perform(get("/workout-groups/delete").param("groupId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/workout-groups/list"));

        verify(workoutGroupRepository, times(1)).deleteById(1L);
    }
}