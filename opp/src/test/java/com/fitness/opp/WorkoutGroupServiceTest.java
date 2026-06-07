package com.fitness.opp;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.WorkoutGroup;
import com.fitness.opp.repository.WorkoutGroupRepository;
import com.fitness.opp.services.WorkoutGroupServiceImpl;
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
public class WorkoutGroupServiceTest {

    @Mock
    private WorkoutGroupRepository workoutGroupRepository;

    @InjectMocks
    private WorkoutGroupServiceImpl workoutGroupService;

    @Test
    void testGetAllGroups() {
        WorkoutGroup group = new WorkoutGroup();
        when(workoutGroupRepository.findAll()).thenReturn(List.of(group));

        List<WorkoutGroup> result = workoutGroupService.getAllGroups();

        assertEquals(1, result.size());
        verify(workoutGroupRepository, times(1)).findAll();
    }

    @Test
    void testSaveGroup() {
        WorkoutGroup group = new WorkoutGroup();
        group.setName("Cardio");


        workoutGroupService.saveGroup(group);

        verify(workoutGroupRepository, times(1)).save(group);
    }

    @Test
    void testGetGroupById_Success() {
        WorkoutGroup group = new WorkoutGroup();
        group.setId(1L);
        when(workoutGroupRepository.findById(1L)).thenReturn(Optional.of(group));

        WorkoutGroup found = workoutGroupService.getGroupById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetGroupById_NotFound() {
        when(workoutGroupRepository.findById(1L)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> workoutGroupService.getGroupById(1L));
    }

    @Test
    void testDeleteGroup() {
        doNothing().when(workoutGroupRepository).deleteById(1L);

        workoutGroupService.deleteGroup(1L);

        verify(workoutGroupRepository, times(1)).deleteById(1L);
    }
}