package com.fitness.opp.repository;

import com.fitness.opp.models.NutritionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionPlanRepository extends JpaRepository<NutritionPlan, Long> {
}