package com.fitness.opp.services;
import com.fitness.opp.models.NutritionPlan;
import java.util.List;

public interface NutritionPlanService {
    List<NutritionPlan> getAllPlans();
    void savePlan(NutritionPlan plan);
    NutritionPlan getPlanById(Long id);
    void deletePlan(Long id);
}