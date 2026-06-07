package com.fitness.opp.services;
import com.fitness.opp.models.WorkoutGroup;
import java.util.List;

public interface WorkoutGroupService {
    List<WorkoutGroup> getAllGroups();
    void saveGroup(WorkoutGroup group);
    WorkoutGroup getGroupById(Long id);
    void deleteGroup(Long id);
}