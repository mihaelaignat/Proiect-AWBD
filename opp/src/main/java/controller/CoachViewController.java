package com.fitness.opp.controller;

import com.fitness.opp.models.Coach;
import com.fitness.opp.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/coaches")
public class CoachViewController {

    @Autowired
    private CoachRepository coachRepository;


    @GetMapping("/list")
    public String listCoaches(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Coach> coachPage = coachRepository.findAll(pageable);

        model.addAttribute("coaches", coachPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coachPage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "coaches-list";
    }


    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        model.addAttribute("coach", new Coach());
        return "add-coach";
    }


    @PostMapping("/save")
    public String saveCoach(@ModelAttribute("coach") Coach coach) {
        coachRepository.save(coach);
        return "redirect:/coaches/list";
    }


    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("coachId") Long id, Model model) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost găsit"));

        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}