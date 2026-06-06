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

    // 1. Listare
    @GetMapping("/list")
    public String listExercises(Model model) {
        model.addAttribute("exercises", exerciseService.getAllExercises());
        return "exercises-list";
    }

    // 2. Formular Adăugare (Am schimbat ruta să se pupe cu HTML-ul)
    @GetMapping("/showFormForAdd")
    public String showAddForm(Model model) {
        model.addAttribute("exercise", new Exercise());
        // Trimitem și grupurile pentru a le putea afișa în dropdown dacă e cazul
        model.addAttribute("allGroups", workoutGroupRepository.findAll());
        return "add-exercise";
    }

    // 3. Salvare
    @PostMapping("/save")
    public String saveExercise(@ModelAttribute("exercise") Exercise exercise) {
        exerciseService.saveExercise(exercise);
        return "redirect:/exercises/list";
    }

    // 4. Update (Lipsea din codul tău - esențial pentru Coverage)
    @GetMapping("/showFormForUpdate")
    public String showUpdateForm(@RequestParam("exerciseId") Long id, Model model) {
        Exercise exercise = exerciseService.getExerciseById(id);
        model.addAttribute("exercise", exercise);
        model.addAttribute("allGroups", workoutGroupRepository.findAll());
        return "add-exercise";
    }

    // 5. Ștergere (Am schimbat din PathVariable în RequestParam pentru consistență cu tabelul)
    @GetMapping("/delete")
    public String deleteExercise(@RequestParam("exerciseId") Long id) {
        exerciseService.deleteExercise(id);
        return "redirect:/exercises/list";
    }
}