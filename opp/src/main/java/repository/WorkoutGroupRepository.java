package com.fitness.opp.repository;

import com.fitness.opp.models.WorkoutGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutGroupRepository extends JpaRepository<WorkoutGroup, Long> {
}