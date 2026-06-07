package com.fitness.opp.controller;

import com.fitness.opp.models.Coach;
import com.fitness.opp.repository.CoachRepository;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CoachViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoachRepository coachRepository;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testListCoachesAsc() throws Exception {
        when(coachRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/coaches/list")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("coaches-list"))
                .andExpect(model().attribute("reverseSortDir", "desc"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testListCoachesDesc() throws Exception {
        when(coachRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/coaches/list")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("reverseSortDir", "asc"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowFormForAdd() throws Exception {
        mockMvc.perform(get("/coaches/showFormForAdd"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-coach"))
                .andExpect(model().attributeExists("coach"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testSaveCoach() throws Exception {
        mockMvc.perform(post("/coaches/save")
                        .with(csrf())
                        .flashAttr("coach", new Coach()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coaches/list"));

        verify(coachRepository, times(1)).save(any(Coach.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowFormForUpdateSuccess() throws Exception {
        Coach coach = new Coach();
        coach.setId(1L);
        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));

        mockMvc.perform(get("/coaches/showFormForUpdate").param("coachId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-coach"))
                .andExpect(model().attribute("coach", coach));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testShowFormForUpdateNotFound() {
        
        when(coachRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            
            mockMvc.perform(get("/coaches/showFormForUpdate").param("coachId", "1"));
        } catch (Exception e) {
            
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("Antrenorul nu a fost gasit"));
        }
        
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void testDeleteCoach() throws Exception {
        mockMvc.perform(get("/coaches/delete").param("coachId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coaches/list"));

        verify(coachRepository, times(1)).deleteById(1L);
    }
}