package com.fitness.opp;

import com.fitness.opp.models.Exercise;
import com.fitness.opp.services.ExerciseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExerciseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExerciseService exerciseService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testListExercises() throws Exception {
        when(exerciseService.getAllExercises()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/exercises/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("exercises-list"))
                .andExpect(model().attributeExists("exercises"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowAddForm() throws Exception {
        // Am corectat ruta de la /add-new la /showFormForAdd
        mockMvc.perform(get("/exercises/showFormForAdd"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-exercise"))
                .andExpect(model().attributeExists("exercise"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testSaveExercise() throws Exception {
        mockMvc.perform(post("/exercises/save")
                        .with(csrf()) // Previne eroarea 403
                        .flashAttr("exercise", new Exercise()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exercises/list"));

        verify(exerciseService, times(1)).saveExercise(any(Exercise.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testDeleteExercise() throws Exception {
        // Am corectat ruta de la /delete/{id} la /delete?exerciseId=id
        mockMvc.perform(get("/exercises/delete")
                        .param("exerciseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exercises/list"));

        verify(exerciseService, times(1)).deleteExercise(1L);
    }
}