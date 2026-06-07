package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.NutritionPlan;
import com.fitness.opp.repository.NutritionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NutritionPlanServiceImpl implements NutritionPlanService {
    @Autowired private NutritionPlanRepository nutritionPlanRepository;

    @Override public List<NutritionPlan> getAllPlans() { return nutritionPlanRepository.findAll(); }

    @Override public void savePlan(NutritionPlan plan) {

        if (plan.getName().length() < 5) throw new RuntimeException("Numele planului este prea scurt!");
        nutritionPlanRepository.save(plan);
    }

    @Override public NutritionPlan getPlanById(Long id) {
        return nutritionPlanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Planul nu a fost gasit"));
    }

    @Override public void deletePlan(Long id) {
        if (!nutritionPlanRepository.existsById(id)) throw new ResourceNotFoundException("Nu exista planul");
        nutritionPlanRepository.deleteById(id);
    }
}