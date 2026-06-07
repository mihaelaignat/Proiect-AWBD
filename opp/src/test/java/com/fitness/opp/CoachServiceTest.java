package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.Coach;
import com.fitness.opp.models.User;
import com.fitness.opp.repository.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachServiceImpl coachService;

    private Coach sampleCoach;

    @BeforeEach
    void setUp() {
        sampleCoach = new Coach();
        sampleCoach.setId(1L);
        sampleCoach.setName("Popescu Ion");
        sampleCoach.setUsers(new ArrayList<>());
    }

    @Test
    void testGetAllCoaches() {
        List<Coach> coaches = List.of(sampleCoach);
        when(coachRepository.findAll()).thenReturn(coaches);

        List<Coach> result = coachService.getAllCoaches();

        assertEquals(1, result.size());
        verify(coachRepository, times(1)).findAll();
    }

    @Test
    void testSaveCoach() {
        coachService.saveCoach(sampleCoach);
        verify(coachRepository, times(1)).save(sampleCoach);
    }

    @Test
    void testGetCoachById_Success() {
        when(coachRepository.findById(1L)).thenReturn(Optional.of(sampleCoach));

        Coach result = coachService.getCoachById(1L);

        assertNotNull(result);
        assertEquals("Popescu Ion", result.getName());
    }

    @Test
    void testGetCoachById_ThrowsException() {
        when(coachRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coachService.getCoachById(1L));
    }

    @Test
    void testDeleteCoach_Success() {
        when(coachRepository.findById(1L)).thenReturn(Optional.of(sampleCoach));

        coachService.deleteCoach(1L);

        verify(coachRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCoach_ThrowsBusinessError() {
        List<User> clients = new ArrayList<>();
        clients.add(new User());
        sampleCoach.setUsers(clients);

        when(coachRepository.findById(1L)).thenReturn(Optional.of(sampleCoach));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> coachService.deleteCoach(1L));
        assertTrue(exception.getMessage().contains("Nu se poate sterge un antrenor care are clienti"));

        verify(coachRepository, never()).deleteById(anyLong());
    }
}