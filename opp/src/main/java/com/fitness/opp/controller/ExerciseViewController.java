package com.fitness.opp.controller;

import com.fitness.opp.models.Exercise;
import com.fitness.opp.services.ExerciseService;
import com.fitness.opp.repository.WorkoutGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/exercises")
public class ExerciseViewController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private WorkoutGroupRepository workoutGroupRepository;

    
    @GetMapping("/list")
    public String listExercises(Model model) {
        model.addAttribute("exercises", exerciseService.getAllExercises());
        return "exercises-list";
    }

    
    @GetMapping("/showFormForAdd")
    public String showAddForm(Model model) {
        model.addAttribute("exercise", new Exercise());
        
        model.addAttribute("allGroups", workoutGroupRepository.findAll());
        return "add-exercise";
    }

    
    @PostMapping("/save")
    public String saveExercise(@ModelAttribute("exercise") Exercise exercise) {
        exerciseService.saveExercise(exercise);
        return "redirect:/exercises/list";
    }

    
    @GetMapping("/showFormForUpdate")
    public String showUpdateForm(@RequestParam("exerciseId") Long id, Model model) {
        Exercise exercise = exerciseService.getExerciseById(id);
        model.addAttribute("exercise", exercise);
        model.addAttribute("allGroups", workoutGroupRepository.findAll());
        return "add-exercise";
    }

    
    @GetMapping("/delete")
    public String deleteExercise(@RequestParam("exerciseId") Long id) {
        exerciseService.deleteExercise(id);
        return "redirect:/exercises/list";
    }
}