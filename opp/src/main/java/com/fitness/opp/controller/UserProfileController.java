package com.fitness.opp.controller;

import com.fitness.opp.models.User;
import com.fitness.opp.models.UserProfile;
import com.fitness.opp.repository.UserProfileRepository;
import com.fitness.opp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/list")
    public String listProfiles(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "id") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 5, sort);
        Page<UserProfile> profilePage = profileRepository.findAll(pageable);

        model.addAttribute("profiles", profilePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", profilePage.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "profiles-list";
    }


    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        model.addAttribute("userProfile", new UserProfile());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("isUpdate", false);
        model.addAttribute("sortField", "id");
        return "profile-form";
    }


    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("profileId") Long id, Model model) {
        UserProfile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profilul nu a fost gasit."));

        model.addAttribute("userProfile", profile);
        model.addAttribute("isUpdate", true);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("sortField", "id");
        return "profile-form";
    }


    @PostMapping("/save")
    public String saveProfile(@ModelAttribute("userProfile") UserProfile profile) {


        if (profile.getId() != null) {
            Optional<UserProfile> existing = profileRepository.findById(profile.getId());
            if (existing.isPresent()) {
                if (profile.getUser() == null || profile.getUser().getId() == null) {
                    profile.setUser(existing.get().getUser());
                }
            }
        }


        if (profile.getUser() == null || profile.getUser().getId() == null) {
            return "redirect:/profile/showFormForAdd?error=missingUser";
        }

        profileRepository.save(profile);
        return "redirect:/profile/list";
    }


    @Transactional
    @GetMapping("/delete")
    public String deleteProfile(@RequestParam("profileId") Long id) {

        Optional<UserProfile> profileOpt = profileRepository.findById(id);

        if (profileOpt.isPresent()) {
            UserProfile profile = profileOpt.get();


            if (profile.getUser() != null) {
                User user = profile.getUser();
                user.setProfile(null);
                userRepository.save(user);
            }


            profileRepository.delete(profile);


            profileRepository.flush();

            System.out.println("DEBUG: Profilul cu ID " + id + " a fost sters.");
        }

        return "redirect:/profile/list";
    }
}