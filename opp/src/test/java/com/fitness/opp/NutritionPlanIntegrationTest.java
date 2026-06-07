package com.fitness.opp;

import com.fitness.opp.controller.NutritionPlanViewController;
import com.fitness.opp.models.NutritionPlan;
import com.fitness.opp.repository.NutritionPlanRepository;
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
public class NutritionPlanIntegrationTest {

    private MockMvc standaloneMockMvc;

    @Autowired
    private NutritionPlanViewController nutritionPlanViewController;

    @Autowired
    private NutritionPlanRepository nutritionPlanRepository;

    @BeforeEach
    void setup() {
        
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        standaloneMockMvc = MockMvcBuilders.standaloneSetup(nutritionPlanViewController)
                .setViewResolvers(viewResolver)
                .build();

        nutritionPlanRepository.deleteAll();
    }

    @Test
    void testListPlans() throws Exception {
        
        NutritionPlan plan = new NutritionPlan();
        plan.setName("Keto Diet");
        plan.setCalories(2000);
        nutritionPlanRepository.save(plan);

        standaloneMockMvc.perform(get("/nutrition/list")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("plans"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(view().name("nutrition-list"));
    }

    @Test
    void testShowFormForAdd() throws Exception {
        standaloneMockMvc.perform(get("/nutrition/showFormForAdd"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("plan"))
                .andExpect(view().name("add-nutrition-plan"));
    }

    @Test
    void testSavePlan() throws Exception {
        standaloneMockMvc.perform(post("/nutrition/save")
                        .param("name", "Vegan Plan")
                        .param("description", "Low carb")
                        .param("calories", "1800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nutrition/list"));

        assertEquals(1, nutritionPlanRepository.findAll().size());
    }

    @Test
    void testShowFormForUpdate() throws Exception {
        NutritionPlan plan = new NutritionPlan();
        plan.setName("To Update");
        plan = nutritionPlanRepository.save(plan);

        
        standaloneMockMvc.perform(get("/nutrition/showFormForUpdate")
                        .param("planId", plan.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("plan"))
                .andExpect(view().name("add-nutrition-plan"));
    }

    @Test
    void testDeletePlan() throws Exception {
        NutritionPlan plan = new NutritionPlan();
        plan.setName("To Delete");
        plan = nutritionPlanRepository.save(plan);

        
        standaloneMockMvc.perform(get("/nutrition/delete")
                        .param("planId", plan.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nutrition/list"));

        assertFalse(nutritionPlanRepository.findById(plan.getId()).isPresent());
    }
}