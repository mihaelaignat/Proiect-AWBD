package com.fitness.opp;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.Exercise;
import com.fitness.opp.repository.ExerciseRepository;
import com.fitness.opp.services.ExerciseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    @Test
    void testGetAllExercises() {
        Exercise ex = new Exercise();
        when(exerciseRepository.findAll()).thenReturn(List.of(ex));

        List<Exercise> result = exerciseService.getAllExercises();

        assertEquals(1, result.size());
        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    void testSaveExercise_Success() {
        Exercise ex = new Exercise();
        ex.setDuration(30);

        exerciseService.saveExercise(ex);

        verify(exerciseRepository, times(1)).save(ex);
    }

    @Test
    void testSaveExercise_BusinessError_WithZeroOrNegative() {
        Exercise ex = new Exercise();
        ex.setDuration(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                exerciseService.saveExercise(ex)
        );

        assertTrue(exception.getMessage().contains("Durata trebuie sa fie un numar pozitiv"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void testSaveExercise_BusinessError_WithNull() {
        Exercise ex = new Exercise();
        ex.setDuration(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                exerciseService.saveExercise(ex)
        );

        assertTrue(exception.getMessage().contains("Durata trebuie sa fie un numar pozitiv"));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void testGetExerciseById_Success() {
        Exercise ex = new Exercise();
        ex.setId(10L);
        when(exerciseRepository.findById(10L)).thenReturn(Optional.of(ex));

        Exercise found = exerciseService.getExerciseById(10L);

        assertNotNull(found);
        assertEquals(10L, found.getId());
    }

    @Test
    void testGetExerciseById_NotFound() {
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                exerciseService.getExerciseById(99L)
        );
    }

    @Test
    void testDeleteExercise_Success() {
        when(exerciseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(exerciseRepository).deleteById(1L);

        exerciseService.deleteExercise(1L);

        verify(exerciseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteExercise_NotFound() {
        when(exerciseRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                exerciseService.deleteExercise(1L)
        );

        verify(exerciseRepository, never()).deleteById(anyLong());
    }
}