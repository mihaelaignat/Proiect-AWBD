package com.fitness.opp.controllers;

import com.fitness.opp.models.Coach;
import com.fitness.opp.models.User;
import com.fitness.opp.services.CoachService;
import com.fitness.opp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserViewController {

    private final UserService userService;
    private final CoachService coachService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserViewController(UserService userService, CoachService coachService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.coachService = coachService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/list")
    public String listUsers(Model theModel,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "sort", defaultValue = "username") String sortField) {

        Page<User> userPage = userService.findPaginated(page, 5, sortField);
        List<Coach> allCoaches = coachService.getAllCoaches();

        theModel.addAttribute("users", userPage.getContent());
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", userPage.getTotalPages());
        theModel.addAttribute("sortField", sortField);
        theModel.addAttribute("newUser", new User());
        theModel.addAttribute("coaches", allCoaches);

        return "users-list";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User theUser,
                           @RequestParam(name = "coachId", required = false) Long coachId) {

        if (theUser.getId() == null || theUser.getId() == 0) {
            theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
        } else {
            User existingUser = userService.findById(theUser.getId());
            if (existingUser != null) {
                theUser.setPassword(existingUser.getPassword());
            }
        }

        if (coachId != null && coachId > 0) {
            try {
                Coach selectedCoach = coachService.getCoachById(coachId);
                theUser.setCoach(selectedCoach);
            } catch (Exception e) {
                theUser.setCoach(null);
            }
        } else {
            theUser.setCoach(null);
        }

        userService.save(theUser);
        return "redirect:/users/list";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long theId) {
        userService.deleteById(theId);
        return "redirect:/users/list";
    }
}