package com.fitness.opp;

import com.fitness.opp.models.Coach;
import com.fitness.opp.models.User;
import com.fitness.opp.repository.CoachRepository;
import com.fitness.opp.services.CoachServiceImpl;
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
public class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachServiceImpl coachService;

    @Test
    void testGetCoachById() {
        Coach coach = new Coach();
        coach.setId(1L);
        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));
        assertNotNull(coachService.getCoachById(1L));
    }

    @Test
    void testDeleteCoach_Error() {
        Coach coach = new Coach();
        coach.setUsers(List.of(new User()));
        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));
        assertThrows(RuntimeException.class, () -> coachService.deleteCoach(1L));
    }
}