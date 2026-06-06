package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.WorkoutGroup;
import com.fitness.opp.repository.WorkoutGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkoutGroupServiceImpl implements WorkoutGroupService {
    @Autowired private WorkoutGroupRepository workoutGroupRepository;

    @Override public List<WorkoutGroup> getAllGroups() { return workoutGroupRepository.findAll(); }

    @Override public void saveGroup(WorkoutGroup group) { workoutGroupRepository.save(group); }

    @Override public WorkoutGroup getGroupById(Long id) {
        return workoutGroupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Grupul nu exista"));
    }

    @Override public void deleteGroup(Long id) {
        workoutGroupRepository.deleteById(id);
    }
}