package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.NutritionPlan;
import com.fitness.opp.repository.NutritionPlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NutritionPlanServiceTest {

    @Mock
    private NutritionPlanRepository nutritionPlanRepository;

    @InjectMocks
    private NutritionPlanServiceImpl nutritionPlanService;

    @Test
    public void testGetAllPlans() {
        when(nutritionPlanRepository.findAll()).thenReturn(Arrays.asList(new NutritionPlan(), new NutritionPlan()));
        List<NutritionPlan> plans = nutritionPlanService.getAllPlans();
        assertEquals(2, plans.size());
        verify(nutritionPlanRepository, times(1)).findAll();
    }

    @Test
    public void testSavePlanSuccess() {
        NutritionPlan plan = new NutritionPlan();
        plan.setName("Plan Vegan Detaliat"); // Mai mult de 5 caractere

        nutritionPlanService.savePlan(plan);

        verify(nutritionPlanRepository, times(1)).save(plan);
    }

    @Test
    public void testSavePlanNameTooShort() {
        NutritionPlan plan = new NutritionPlan();
        plan.setName("Mic"); // Sub 5 caractere

        // Aceasta va acoperi linia cu throw new RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> {
            nutritionPlanService.savePlan(plan);
        });

        assertEquals("Numele planului este prea scurt!", exception.getMessage());
        verify(nutritionPlanRepository, never()).save(any());
    }

    @Test
    public void testGetPlanByIdSuccess() {
        NutritionPlan plan = new NutritionPlan();
        plan.setId(1L);
        when(nutritionPlanRepository.findById(1L)).thenReturn(Optional.of(plan));

        NutritionPlan result = nutritionPlanService.getPlanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetPlanByIdNotFound() {
        when(nutritionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        // Acoperă ramura orElseThrow
        assertThrows(ResourceNotFoundException.class, () -> {
            nutritionPlanService.getPlanById(1L);
        });
    }

    @Test
    public void testDeletePlanSuccess() {
        when(nutritionPlanRepository.existsById(1L)).thenReturn(true);

        nutritionPlanService.deletePlan(1L);

        verify(nutritionPlanRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePlanNotFound() {
        when(nutritionPlanRepository.existsById(1L)).thenReturn(false);

        // Acoperă eroarea de la delete
        assertThrows(ResourceNotFoundException.class, () -> {
            nutritionPlanService.deletePlan(1L);
        });

        verify(nutritionPlanRepository, never()).deleteById(anyLong());
    }
}