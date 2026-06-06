package com.fitness.opp.controller;

import com.fitness.opp.models.WorkoutGroup;
import com.fitness.opp.models.Exercise;
import com.fitness.opp.repository.WorkoutGroupRepository;
import com.fitness.opp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/workout-groups")
public class WorkoutGroupController {

    @Autowired
    private WorkoutGroupRepository workoutGroupRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @GetMapping("/list")
    public String listGroups(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WorkoutGroup> groupPage = workoutGroupRepository.findAll(pageable);

        model.addAttribute("groups", groupPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", groupPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "workout-groups-list";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        model.addAttribute("group", new WorkoutGroup());
        model.addAttribute("allExercises", exerciseRepository.findAll());
        return "add-workout-group";
    }

    @PostMapping("/save")
    public String saveGroup(@ModelAttribute("group") WorkoutGroup group,
                            @RequestParam(value = "exerciseIds", required = false) List<Long> exerciseIds) {

        if (exerciseIds != null) {
            List<Exercise> selectedExercises = exerciseRepository.findAllById(exerciseIds);
            group.setExercises(selectedExercises);
        }

        workoutGroupRepository.save(group);
        return "redirect:/workout-groups/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("groupId") Long id, Model model) {
        WorkoutGroup group = workoutGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupul nu a fost gasit"));
        model.addAttribute("group", group);
        model.addAttribute("allExercises", exerciseRepository.findAll());
        return "add-workout-group";
    }

    @GetMapping("/delete")
    public String deleteGroup(@RequestParam("groupId") Long id) {
        workoutGroupRepository.deleteById(id);
        return "redirect:/workout-groups/list";
    }
}