package com.fitness.opp.services;

import com.fitness.opp.models.Exercise;
import java.util.List;

public interface ExerciseService {
    List<Exercise> getAllExercises();
    void saveExercise(Exercise exercise);
    Exercise getExerciseById(Long id);
    void deleteExercise(Long id);
}