package com.fitness.opp.controller;

import com.fitness.opp.models.NutritionPlan;
import com.fitness.opp.repository.NutritionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nutrition")
public class NutritionPlanViewController {

    @Autowired
    private NutritionPlanRepository nutritionPlanRepository;

    @GetMapping("/list")
    public String listPlans(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<NutritionPlan> planPage = nutritionPlanRepository.findAll(pageable);

        model.addAttribute("plans", planPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", planPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "nutrition-list";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        model.addAttribute("plan", new NutritionPlan());
        return "add-nutrition-plan";
    }

    @PostMapping("/save")
    public String savePlan(@ModelAttribute("plan") NutritionPlan plan) {
        nutritionPlanRepository.save(plan);
        return "redirect:/nutrition/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("planId") Long id, Model model) {
        NutritionPlan plan = nutritionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planul nu a fost găsit"));
        model.addAttribute("plan", plan);
        return "add-nutrition-plan";
    }

    @GetMapping("/delete")
    public String deletePlan(@RequestParam("planId") Long id) {
        nutritionPlanRepository.deleteById(id);
        return "redirect:/nutrition/list";
    }
}