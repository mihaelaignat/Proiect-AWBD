package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.Exercise;
import com.fitness.opp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @Override
    public void saveExercise(Exercise exercise) {
        if (exercise.getDuration() == null || exercise.getDuration() <= 0) {
            throw new RuntimeException("Business Error: Durata trebuie sa fie un numar pozitiv!");
        }
        exerciseRepository.save(exercise);
    }

    @Override
    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercitiul cu ID " + id + " nu a fost gasit."));
    }

    @Override
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercitiul nu exista.");
        }
        exerciseRepository.deleteById(id);
    }
}