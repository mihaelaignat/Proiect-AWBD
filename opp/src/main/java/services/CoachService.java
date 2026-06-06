package com.fitness.opp.services;

import com.fitness.opp.models.Coach;
import java.util.List;

public interface CoachService {
    List<Coach> getAllCoaches();
    void saveCoach(Coach coach);
    Coach getCoachById(Long id);
    void deleteCoach(Long id);
}