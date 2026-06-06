PARTEA I: CERINȚE OBLIGATORII (60%)
1. Model de Date (10%) 
Cerințe: Lab2
•	Minimum 6-7 entități interconectate
•	Relații de toate tipurile:
- @OneToOne (min. 1 exemplu)
- @OneToMany / @ManyToOne (min. 2 exemple)
- @ManyToMany (min. 1 exemplu)
•	Diagrama ER documentată în README
Criterii evaluare:
•	Complexitate model de date
•	Relevanța relațiilor pentru domeniul ales
•	Documentație



 
Coach: 

package com.fitness.opp.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "coaches")
@Data
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;

    @OneToMany(mappedBy = "coach")
    private List<User> users;
}

Exercise:

package com.fitness.opp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
@Data
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele exercitiului este obligatoriu")
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "muscle_group")
    private String muscleGroup;

    @Column(name = "sets")
    private Integer sets;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "duration")
    private Integer duration;

    @ManyToMany(mappedBy = "exercises")
    private List<WorkoutGroup> workoutGroups = new ArrayList<>();

    public Exercise() {}
}

NutritionPlan:

package com.fitness.opp.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "nutrition_plans")
@Data
public class NutritionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer calories;

    @OneToMany(mappedBy = "nutritionPlan", cascade = CascadeType.ALL)
    private List<User> users;
}

User:
package com.fitness.opp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username-ul este obligatoriu")
    @Column(unique = true)
    private String username;

    private String password;
    private String email;
    private String role;

    @ManyToOne
    @JoinColumn(name = "nutrition_plan_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NutritionPlan nutritionPlan;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Coach coach;

     @ManyToOne
     @JoinColumn(name = "workout_group_id")
     @ToString.Exclude
     @EqualsAndHashCode.Exclude
	private WorkoutGroup workoutGroup;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserProfile profile;
}

UserProfile:
package com.fitness.opp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_profiles")
@Data
@EqualsAndHashCode(exclude = "user")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    private Integer age;

    @Column(name = "WEIGHT_KG")
    private Double weight;

    private String fitnessGoal;
}

WorkoutGroup:

package com.fitness.opp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WORKOUT_GROUPS")
@Data
public class WorkoutGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    @NotBlank(message = "Numele grupului este obligatoriu")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CATEGORY")
    private String category;

	@OneToMany(mappedBy = "workoutGroup") 
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<User> members = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "workout_group_exercises",
            joinColumns = @JoinColumn(name = "workout_group_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private List<Exercise> exercises = new ArrayList<>();

    public WorkoutGroup() {}
}

2. Operații CRUD Complete (8%)
Cerințe: Lab2
•	Create, Read, Update, Delete pentru toate entitățile
•	Repository pattern cu Spring Data JPA
•	Service layer cu logică de business
•	Exception handling specific pentru fiecare operație
Criterii evaluare:
•	Implementare completă CRUD
•	Calitatea codului și separarea responsabilităților
Tratarea excepțiilor



Create, Read, Update, Delete pentru toate entitățile

Create: add-coaches.html (introducere date)

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Salvare Antrenor</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="container mt-5">

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow">
            <div class="card-header bg-dark text-white">
                <h4 class="mb-0">
                    <i class="fas fa-user-tie"></i>
                    <span th:text="${coach.id == null} ? 'Adauga Antrenor' : 'Editeaza Antrenor'"></span>
                </h4>
            </div>
            <div class="card-body">
                <form th:action="@{/coaches/save}" th:object="${coach}" method="POST">

                    <input type="hidden" th:field="*{id}" />

                    <div class="mb-3">
                        <label class="form-label fw-bold">Nume Complet:</label>
                        <input type="text" th:field="*{name}" class="form-control" placeholder="Ex: Andrei Ionescu" required />
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Specializare:</label>
                        <input type="text" th:field="*{specialization}" class="form-control" placeholder="Ex: Cardio, Bodybuilding" required />
                    </div>

                    <div class="d-grid gap-2 d-md-block mt-4 text-center">
                        <button type="submit" class="btn btn-success px-5">
                            <i class="fas fa-save"></i> Salveaza
                        </button>
                        <a th:href="@{/coaches/list}" class="btn btn-secondary px-4">Anuleaza</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>

Controller: @PostMapping – primeste date din formular

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
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost gasit"));

        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}
Service:  save() apeleaza repository

package com.fitness.opp.services;


import com.fitness.opp.exceptions.ResourceNotFoundException;

import com.fitness.opp.models.Coach;

import com.fitness.opp.repository.CoachRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service

public class CoachServiceImpl implements CoachService {


    @Autowired

    private CoachRepository coachRepository;


    @Override

    public List<Coach> getAllCoaches() {

        return coachRepository.findAll();

    }


    @Override

    public void saveCoach(Coach coach) {

        coachRepository.save(coach);

    }


    @Override

    public Coach getCoachById(Long id) {

        return coachRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Antrenorul cu ID " + id + " nu a fost gasit."));

    }


    @Override

    public void deleteCoach(Long id) {

        Coach coach = getCoachById(id);

        if (coach.getUsers() != null && !coach.getUsers().isEmpty()) {

            throw new RuntimeException("Business Error: Nu se poate sterge un antrenor care are clienti!");

        }

        coachRepository.deleteById(id);

    }

}

Repository: 

package com.fitness.opp.repository;

import com.fitness.opp.models.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
}

Read: coaches-list.html

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>Gestiune Antrenori</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4 px-3 shadow">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><i class="fas fa-dumbbell text-info"></i> Fitness Admin</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" th:href="@{/users/list}">Utilizatori</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/profile/list}">Editare Profil</a></li>
                <li class="nav-item"><a class="nav-link active" th:href="@{/coaches/list}">Antrenori</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/workout-groups/list}">Workouts</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/exercises/list}">Exercitii</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/nutrition/list}">Nutritie</a></li>
            </ul>
            <form th:action="@{/logout}" method="post" class="d-flex">
                <button type="submit" class="btn btn-outline-light btn-sm border-0">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </button>
            </form>
        </div>
    </div>
</nav>

<div class="container bg-white p-4 shadow-sm rounded">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3><i class="fas fa-user-tie text-primary"></i> Echipa de Antrenori</h3>
        
        <a sec:authorize="hasAuthority('ADMIN')" th:href="@{/coaches/showFormForAdd}" class="btn btn-success">
            <i class="fas fa-plus"></i> Adauga Antrenor
        </a>
    </div>

    <table class="table table-hover border text-center">
        <thead class="table-dark">
        <tr>
            <th><a class="text-white text-decoration-none" th:href="@{/coaches/list(page=${currentPage}, sortField='name', sortDir=${reverseSortDir})}">Nume <i class="fas fa-sort"></i></a></th>
            <th><a class="text-white text-decoration-none" th:href="@{/coaches/list(page=${currentPage}, sortField='specialization', sortDir=${reverseSortDir})}">Specializare <i class="fas fa-sort"></i></a></th>
            
            <th sec:authorize="hasAuthority('ADMIN')">Actiuni</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="coach : ${coaches}">
            <td th:text="${coach.name}" class="fw-bold align-middle"></td>
            <td th:text="${coach.specialization}" class="align-middle"></td>

            
            <td class="align-middle" sec:authorize="hasAuthority('ADMIN')">
                <a th:href="@{/coaches/showFormForUpdate(coachId=${coach.id})}" class="btn btn-sm btn-outline-primary">
                    <i class="fas fa-edit"></i>
                </a>
                <a th:href="@{/coaches/delete(coachId=${coach.id})}" class="btn btn-sm btn-outline-danger"
                   onclick="return confirm('Stergi antrenorul?')">
                    <i class="fas fa-trash"></i>
                </a>
            </td>
        </tr>
        </tbody>
    </table>

    <nav th:if="${totalPages > 1}">
        <ul class="pagination justify-content-center mt-3">
            <li class="page-item" th:classappend="${currentPage == 0} ? 'disabled'"><a class="page-link" th:href="@{/coaches/list(page=${currentPage - 1}, sortField=${sortField}, sortDir=${sortDir})}">Inapoi</a></li>
            <li class="page-item" th:each="i : ${#numbers.sequence(0, totalPages - 1)}" th:classappend="${currentPage == i} ? 'active'"><a class="page-link" th:href="@{/coaches/list(page=${i}, sortField=${sortField}, sortDir=${sortDir})}" th:text="${i + 1}"></a></li>
            <li class="page-item" th:classappend="${currentPage == totalPages - 1} ? 'disabled'"><a class="page-link" th:href="@{/coaches/list(page=${currentPage + 1}, sortField=${sortField}, sortDir=${sortDir})}">Inainte</a></li>
        </ul>
    </nav>
</div>
</body>
</html>

Controller: @GetMapping("/list") cere date de la service

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
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost gasit"));

        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}

Service: findAll()

package com.fitness.opp.services;


import com.fitness.opp.exceptions.ResourceNotFoundException;

import com.fitness.opp.models.Coach;

import com.fitness.opp.repository.CoachRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service

public class CoachServiceImpl implements CoachService {


    @Autowired

    private CoachRepository coachRepository;


    @Override

    public List<Coach> getAllCoaches() {

        return coachRepository.findAll();

    }


    @Override

    public void saveCoach(Coach coach) {

        coachRepository.save(coach);

    }


    @Override

    public Coach getCoachById(Long id) {

        return coachRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Antrenorul cu ID " + id + " nu a fost gasit."));

    }


    @Override

    public void deleteCoach(Long id) {

        Coach coach = getCoachById(id);

        if (coach.getUsers() != null && !coach.getUsers().isEmpty()) {

            throw new RuntimeException("Business Error: Nu se poate sterge un antrenor care are clienti!");

        }

        coachRepository.deleteById(id);

    }

}

Update: add-coaches.html

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Salvare Antrenor</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="container mt-5">

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow">
            <div class="card-header bg-dark text-white">
                <h4 class="mb-0">
                    <i class="fas fa-user-tie"></i>
                    <span th:text="${coach.id == null} ? 'Adauga Antrenor' : 'Editeaza Antrenor'"></span>
                </h4>
            </div>
            <div class="card-body">
                <form th:action="@{/coaches/save}" th:object="${coach}" method="POST">

                    <input type="hidden" th:field="*{id}" />

                    <div class="mb-3">
                        <label class="form-label fw-bold">Nume Complet:</label>
                        <input type="text" th:field="*{name}" class="form-control" placeholder="Ex: Andrei Ionescu" required />
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Specializare:</label>
                        <input type="text" th:field="*{specialization}" class="form-control" placeholder="Ex: Cardio, Bodybuilding" required />
                    </div>

                    <div class="d-grid gap-2 d-md-block mt-4 text-center">
                        <button type="submit" class="btn btn-success px-5">
                            <i class="fas fa-save"></i> Salveaza
                        </button>
                        <a th:href="@{/coaches/list}" class="btn btn-secondary px-4">Anuleaza</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>

Controller: showFormForUpdate

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
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost gasit"));

        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}

Delete: butonul “Delete”

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>Gestiune Antrenori</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4 px-3 shadow">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><i class="fas fa-dumbbell text-info"></i> Fitness Admin</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" th:href="@{/users/list}">Utilizatori</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/profile/list}">Editare Profil</a></li>
                <li class="nav-item"><a class="nav-link active" th:href="@{/coaches/list}">Antrenori</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/workout-groups/list}">Workouts</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/exercises/list}">Exercitii</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/nutrition/list}">Nutritie</a></li>
            </ul>
            <form th:action="@{/logout}" method="post" class="d-flex">
                <button type="submit" class="btn btn-outline-light btn-sm border-0">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </button>
            </form>
        </div>
    </div>
</nav>

<div class="container bg-white p-4 shadow-sm rounded">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3><i class="fas fa-user-tie text-primary"></i> Echipa de Antrenori</h3>
        
        <a sec:authorize="hasAuthority('ADMIN')" th:href="@{/coaches/showFormForAdd}" class="btn btn-success">
            <i class="fas fa-plus"></i> Adauga Antrenor
        </a>
    </div>

    <table class="table table-hover border text-center">
        <thead class="table-dark">
        <tr>
            <th><a class="text-white text-decoration-none" th:href="@{/coaches/list(page=${currentPage}, sortField='name', sortDir=${reverseSortDir})}">Nume <i class="fas fa-sort"></i></a></th>
            <th><a class="text-white text-decoration-none" th:href="@{/coaches/list(page=${currentPage}, sortField='specialization', sortDir=${reverseSortDir})}">Specializare <i class="fas fa-sort"></i></a></th>
            
            <th sec:authorize="hasAuthority('ADMIN')">Actiuni</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="coach : ${coaches}">
            <td th:text="${coach.name}" class="fw-bold align-middle"></td>
            <td th:text="${coach.specialization}" class="align-middle"></td>

            
            <td class="align-middle" sec:authorize="hasAuthority('ADMIN')">
                <a th:href="@{/coaches/showFormForUpdate(coachId=${coach.id})}" class="btn btn-sm btn-outline-primary">
                    <i class="fas fa-edit"></i>
                </a>
                <a th:href="@{/coaches/delete(coachId=${coach.id})}" class="btn btn-sm btn-outline-danger"
                   onclick="return confirm('Stergi antrenorul?')">
                    <i class="fas fa-trash"></i>
                </a>
            </td>
        </tr>
        </tbody>
    </table>

    <nav th:if="${totalPages > 1}">
        <ul class="pagination justify-content-center mt-3">
            <li class="page-item" th:classappend="${currentPage == 0} ? 'disabled'"><a class="page-link" th:href="@{/coaches/list(page=${currentPage - 1}, sortField=${sortField}, sortDir=${sortDir})}">Inapoi</a></li>
            <li class="page-item" th:each="i : ${#numbers.sequence(0, totalPages - 1)}" th:classappend="${currentPage == i} ? 'active'"><a class="page-link" th:href="@{/coaches/list(page=${i}, sortField=${sortField}, sortDir=${sortDir})}" th:text="${i + 1}"></a></li>
            <li class="page-item" th:classappend="${currentPage == totalPages - 1} ? 'disabled'"><a class="page-link" th:href="@{/coaches/list(page=${currentPage + 1}, sortField=${sortField}, sortDir=${sortDir})}">Inainte</a></li>
        </ul>
    </nav>
</div>
</body>
</html>

Controller: butonul “Delete”

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
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost gasit"));

        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}




Repository pattern cu Spring Data JPA:

package com.fitness.opp.repository;

import com.fitness.opp.models.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
}

“Spring Data JPA genereaza implementarea automat la runtime (in timpul rularii) folosind concepte de Dynamic Proxy”

Service layer cu logica de business:
CoachService: (interfata)

package com.fitness.opp.services;

import com.fitness.opp.models.Coach;
import java.util.List;

public interface CoachService {
    List<Coach> getAllCoaches();
    void saveCoach(Coach coach);
    Coach getCoachById(Long id);
    void deleteCoach(Long id);
} 

CoachServiceImpl: (implementarea) @Service
package com.fitness.opp.services;
import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.Coach;
import com.fitness.opp.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service

public class CoachServiceImpl implements CoachService {


    @Autowired

    private CoachRepository coachRepository;


    @Override

    public List<Coach> getAllCoaches() {

        return coachRepository.findAll();

    }


    @Override

    public void saveCoach(Coach coach) {

        coachRepository.save(coach);

    }


    @Override

    public Coach getCoachById(Long id) {

        return coachRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Antrenorul cu ID " + id + " nu a fost gasit."));

    }


    @Override

    public void deleteCoach(Long id) {

        Coach coach = getCoachById(id);

        if (coach.getUsers() != null && !coach.getUsers().isEmpty()) {

            throw new RuntimeException("Business Error: Nu se poate sterge un antrenor care are clienti!");

        }

        coachRepository.deleteById(id);

    }

}




Exception handling specific pentru fiecare operație:
ResourceNotFoundException:
package com.fitness.opp.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
CoachServiceImpl: orElseThrow

package com.fitness.opp.services;


import com.fitness.opp.exceptions.ResourceNotFoundException;

import com.fitness.opp.models.Coach;

import com.fitness.opp.repository.CoachRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;


@Service

public class CoachServiceImpl implements CoachService {


    @Autowired

    private CoachRepository coachRepository;


    @Override

    public List<Coach> getAllCoaches() {

        return coachRepository.findAll();

    }


    @Override

    public void saveCoach(Coach coach) {

        coachRepository.save(coach);

    }


    @Override

    public Coach getCoachById(Long id) {

        return coachRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("Antrenorul cu ID " + id + " nu a fost gasit."));

    }


    @Override

    public void deleteCoach(Long id) {

        Coach coach = getCoachById(id);

        if (coach.getUsers() != null && !coach.getUsers().isEmpty()) {

            throw new RuntimeException("Business Error: Nu se poate sterge un antrenor care are clienti!");

        }

        coachRepository.deleteById(id);

    }

}

404.html:

<!DOCTYPE html>
<html>
<head><title>404 Not Found</title></head>
<body style="text-align: center; padding-top: 50px; font-family: sans-serif;">
<h1>404 - Pagina nu exista</h1>
<p>Ne pare rau, dar pagina cautata nu a fost gasita.</p>
<a href="/users/list">Inapoi la lista</a>
</body>
</html>

3. Configurare Multi-Environment (5%)
Cerințe: Lab2
•	Minimum 2 profiluri Spring (dev, test)
•	Configurare pentru minimum 2 baze de date diferite:
- Una pentru dezvoltare (PostgreSQL/MySQL)
- Una pentru testare (H2 in-memory sau separată)
•	Fișiere de configurare separate (application-dev.yml, application-test.yml)
Criterii evaluare:
•	Configurare profiles (3p)
•	Separarea corectă a mediilor (3p)

Application.properties:

spring.profiles.active=dev


spring.application.name=FitnessOPP-MasterProject


server.port=8080


spring.security.csrf.enabled=true


spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML


logging.level.root=INFO
logging.level.com.fitness.opp=DEBUG

application-dev.properties:


spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=SYSTEM
spring.datasource.password=Hkn1b6dmrtpj
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver


spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.properties.hibernate.format_sql=true


logging.file.name=logs/fitness-app.log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE


spring.jackson.serialization.fail-on-empty-beans=false


server.error.include-message=always
server.error.include-binding-errors=always

application-test.properties:


spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=


spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true


spring.h2.console.enabled=true
spring.h2.console.path=/h2-console


4. Testing (7%)
Cerințe: Lab 4
•	Unit tests: minimum 70% coverage pentru service layer
•	Integration tests: minimum 3 scenarii end-to-end
•	Utilizare JUnit 5 + Mockito
•	Test database configuration
Criterii evaluare:
•	Unit tests
•	Integration tests
•	Code coverage
CoachServiceTest: @Mock si @InjectMocks

package com.fitness.opp;

import com.fitness.opp.models.Coach;
import com.fitness.opp.models.User;
import com.fitness.opp.repository.CoachRepository;
import com.fitness.opp.services.CoachServiceImpl;
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
public class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachServiceImpl coachService;

    @Test
    void testGetCoachById() {
        Coach coach = new Coach();
        coach.setId(1L);
        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));
        assertNotNull(coachService.getCoachById(1L));
    }

    @Test
    void testDeleteCoach_Error() {
        Coach coach = new Coach();
        coach.setUsers(List.of(new User()));
        when(coachRepository.findById(1L)).thenReturn(Optional.of(coach));
        assertThrows(RuntimeException.class, () -> coachService.deleteCoach(1L));
    }
}

CoachIntegrationTest:

package com.fitness.opp;

import com.fitness.opp.models.Coach;
import com.fitness.opp.repository.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class CoachIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CoachRepository coachRepository;

    @BeforeEach
    void setup() {
        coachRepository.deleteAll();
    }

    @Test
    void testListCoaches() throws Exception {
        mockMvc.perform(get("/coaches/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("coaches-list"));
    }

    @Test
    void testSaveCoach() throws Exception {
        mockMvc.perform(post("/coaches/save")
                        .with(csrf())
                        .param("name", "Antrenor Test")
                        .param("specialization", "Fitness"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coaches/list"));

        assertEquals(1, coachRepository.findAll().size());
    }

    @Test
    void testDeleteCoach() throws Exception {
        Coach coach = new Coach();
        coach.setName("De Sters");
        coach.setSpecialization("Yoga");
        coach = coachRepository.save(coach);

        mockMvc.perform(get("/coaches/delete")
                        .param("coachId", coach.getId().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/coaches/list"));

        assertFalse(coachRepository.findById(coach.getId()).isPresent());
    }
}


Application-test.properties:

spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=


spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true


spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

5. Views și Validare (10%)
Cerințe: Lab 4
•	Frontend: Thymeleaf/JSP sau framework modern (React/Vue/Angular)
•	Formulare: pentru toate operațiile CRUD
•	Validare:
- Server-side cu Bean Validation (@Valid, @NotNull, etc.)
- Client-side validation
- Mesaje de eroare user-friendly
•	Exception handling: pagini de eroare custom (404, 500, etc.)


Folosim Thymeleaf ca motor de template-uri:

 

Add-coach.html:

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Salvare Antrenor</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="container mt-5">

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow">
            <div class="card-header bg-dark text-white">
                <h4 class="mb-0">
                    <i class="fas fa-user-tie"></i>
                    <span th:text="${coach.id == null} ? 'Adauga Antrenor' : 'Editeaza Antrenor'"></span>
                </h4>
            </div>
            <div class="card-body">
                <form th:action="@{/coaches/save}" th:object="${coach}" method="POST">

                    <input type="hidden" th:field="*{id}" />

                    <div class="mb-3">
                        <label class="form-label fw-bold">Nume Complet:</label>
                        <input type="text" th:field="*{name}" class="form-control" placeholder="Ex: Andrei Ionescu" required />
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Specializare:</label>
                        <input type="text" th:field="*{specialization}" class="form-control" placeholder="Ex: Cardio, Bodybuilding" required />
                    </div>

                    <div class="d-grid gap-2 d-md-block mt-4 text-center">
                        <button type="submit" class="btn btn-success px-5">
                            <i class="fas fa-save"></i> Salveaza
                        </button>
                        <a th:href="@{/coaches/list}" class="btn btn-secondary px-4">Anuleaza</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>


User.java: validare

package com.fitness.opp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username-ul este obligatoriu")
    @Column(unique = true)
    private String username;

    private String password;
    private String email;
    private String role;

    @ManyToOne
    @JoinColumn(name = "nutrition_plan_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NutritionPlan nutritionPlan;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Coach coach;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserProfile profile;
}

404.html:

<!DOCTYPE html>
<html>
<head><title>404 Not Found</title></head>
<body style="text-align: center; padding-top: 50px; font-family: sans-serif;">
<h1>404 - Pagina nu exista</h1>
<p>Ne pare rau, dar pagina cautata nu a fost gasita.</p>
<a href="/users/list">Inapoi la lista</a>
</body>
</html>

6. Logging (4%)
Cerințe:
•	Framework: SLF4J + Logback/Log4j2
•	Nivele de logging configurate corect (INFO, DEBUG, ERROR)
•	Logging în fișiere separate pentru erori
•	[Opțional] Aspecte pentru logging automat
Criterii evaluare:
•	Configurare logging
•	Utilizare adecvată în cod
Application-dev.properties:


spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=SYSTEM
spring.datasource.password=Hkn1b6dmrtpj
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver


spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.properties.hibernate.format_sql=true


logging.file.name=logs/fitness-app.log
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE


spring.jackson.serialization.fail-on-empty-beans=false


server.error.include-message=always
server.error.include-binding-errors=always


logback-spring.xml


<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml" />
    <include resource="org/springframework/boot/logging/logback/console-appender.xml" />

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/app.log</file>
        <encoder><pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern></encoder>
    </appender>

    <appender name="ERROR_FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/errors.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder><pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern></encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
        <appender-ref ref="ERROR_FILE" />
    </root>
</configuration>

7. Paginare și Sortare (6%)
Cerințe:
•	Implementare Pageable pentru minimum 3 entități
•	Opțiuni de sortare după minim 2 criterii per entitate
•	UI pentru navigare între pagini
•	Configurare dimensiune pagină
Criterii evaluare:
•	Implementare backend
•	Integrare frontend
CoachViewController:
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
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost gasit"));
        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}
UserViewController:

package com.fitness.opp.controllers;

import com.fitness.opp.models.User;
import com.fitness.opp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserViewController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserViewController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/list")
    public String listUsers(Model theModel,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "sort", defaultValue = "username") String sortField) {

        Page<User> userPage = userService.findPaginated(page, 5, sortField);

        theModel.addAttribute("users", userPage.getContent());
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", userPage.getTotalPages());
        theModel.addAttribute("sortField", sortField);
        theModel.addAttribute("newUser", new User());

        return "users-list";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User theUser) {
        if (theUser.getId() == null || theUser.getId() == 0) {
            theUser.setPassword(passwordEncoder.encode(theUser.getPassword()));
        } else {
            User existingUser = userService.findById(theUser.getId());
            if (existingUser != null) {
                theUser.setPassword(existingUser.getPassword());
            }
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

NutritionPlanViewController:

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
                .orElseThrow(() -> new RuntimeException("Planul nu a fost gasit"));
        model.addAttribute("plan", plan);
        return "add-nutrition-plan";
    }

    @GetMapping("/delete")
    public String deletePlan(@RequestParam("planId") Long id) {
        nutritionPlanRepository.deleteById(id);
        return "redirect:/nutrition/list";
    }
}

Users-list: avem buton “Next”
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>Gestiune Utilizatori</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4 px-3 shadow">
    <div class="container-fluid">
        <a class="navbar-brand" href="#"><i class="fas fa-dumbbell text-info"></i> Fitness Admin</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link active" th:href="@{/users/list}">Utilizatori</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/profile/list}">Editare Profil</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/coaches/list}">Antrenori</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/workout-groups/list}">Workouts</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/exercises/list}">Exercitii</a></li>
                <li class="nav-item"><a class="nav-link" th:href="@{/nutrition/list}">Nutritie</a></li>
            </ul>
            <form th:action="@{/logout}" method="post" class="d-flex">
                <button type="submit" class="btn btn-outline-light btn-sm border-0"><i class="fas fa-sign-out-alt"></i> Logout</button>
            </form>
        </div>
    </div>
</nav>

<div class="container bg-white p-4 shadow-sm rounded">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3><i class="fas fa-users text-primary"></i> Membri Sistem</h3>
        <button sec:authorize="hasAuthority('ADMIN')" type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addUserModal">
            <i class="fas fa-plus"></i> Adauga Utilizator
        </button>
    </div>

    <table class="table table-hover border text-center">
        <thead class="table-dark">
        <tr>
            <th><a class="text-white text-decoration-none" th:href="@{/users/list(page=${currentPage}, sort='username')}">Username <i class="fas fa-sort"></i></a></th>
            <th><a class="text-white text-decoration-none" th:href="@{/users/list(page=${currentPage}, sort='email')}">Email <i class="fas fa-sort"></i></a></th>
            <th>Rol</th>
            <th sec:authorize="hasAuthority('ADMIN')">Actiuni</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="tempUser : ${users}">
            <td th:text="${tempUser.username}" class="fw-bold align-middle"></td>
            <td th:text="${tempUser.email}" class="align-middle"></td>
            <td class="align-middle"><span class="badge bg-secondary" th:text="${tempUser.role}"></span></td>
            <td class="align-middle" sec:authorize="hasAuthority('ADMIN')">
                <div class="btn-group">
                    <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" th:data-bs-target="'#editModal' + ${tempUser.id}"><i class="fas fa-edit"></i></button>
                    <a th:href="@{/users/delete(userId=${tempUser.id})}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Stergi utilizatorul?')"><i class="fas fa-trash"></i></a>
                </div>

                
                <div class="modal fade" th:id="'editModal' + ${tempUser.id}" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog text-start"><div class="modal-content">
                        <div class="modal-header bg-primary text-white"><h5 class="modal-title">Editeaza Utilizator</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
                        <form th:action="@{/users/save}" method="POST">
                            <div class="modal-body">
                                <input type="hidden" name="id" th:value="${tempUser.id}" />
                                <div class="mb-3"><label class="form-label">Username</label><input type="text" name="username" th:value="${tempUser.username}" class="form-control" required></div>
                                <div class="mb-3"><label class="form-label">Email</label><input type="email" name="email" th:value="${tempUser.email}" class="form-control" required></div>
                                <div class="mb-3">
                                    <label class="form-label">Rol</label>
                                    <select name="role" class="form-select">
                                        <option value="USER" th:selected="${tempUser.role == 'USER'}">USER</option>
                                        <option value="ADMIN" th:selected="${tempUser.role == 'ADMIN'}">ADMIN</option>
                                        <option value="COACH" th:selected="${tempUser.role == 'COACH'}">COACH</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer"><button type="submit" class="btn btn-primary">Salveaza Modificari</button></div>
                        </form>
                    </div></div>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    
    <nav th:if="${totalPages > 1}">
        <ul class="pagination justify-content-center mt-3">
            <li class="page-item" th:classappend="${currentPage == 0} ? 'disabled'"><a class="page-link" th:href="@{/users/list(page=${currentPage - 1}, sort=${sortField})}">Inapoi</a></li>
            <li class="page-item" th:each="i : ${#numbers.sequence(0, totalPages - 1)}" th:classappend="${currentPage == i} ? 'active'"><a class="page-link" th:href="@{/users/list(page=${i}, sort=${sortField})}" th:text="${i + 1}"></a></li>
            <li class="page-item" th:classappend="${currentPage == totalPages - 1} ? 'disabled'"><a class="page-link" th:href="@{/users/list(page=${currentPage + 1}, sort=${sortField})}">Înainte</a></li>
        </ul>
    </nav>
</div>

<!-- Modal Adaugă -->
<div class="modal fade" id="addUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog text-start"><div class="modal-content">
        <div class="modal-header bg-success text-white"><h5 class="modal-title">Adaugă Utilizator Nou</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
        <form th:action="@{/users/save}" th:object="${newUser}" method="POST">
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">Username</label><input type="text" th:field="*{username}" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Email</label><input type="email" th:field="*{email}" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Parolă</label><input type="password" th:field="*{password}" class="form-control" required></div>
                <div class="mb-3">
                    <label class="form-label">Rol</label>
                    <select th:field="*{role}" class="form-select">
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                        <option value="COACH">COACH</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer"><button type="submit" class="btn btn-success">Creeaza Cont</button></div>
        </form>
    </div></div>
</div>

</body>
</html>

CoachViewController: avem “size” 

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
                .orElseThrow(() -> new RuntimeException("Antrenorul nu a fost gasit"));

        model.addAttribute("coach", coach);
        return "add-coach";
    }


    @GetMapping("/delete")
    public String deleteCoach(@RequestParam("coachId") Long id) {
        coachRepository.deleteById(id);
        return "redirect:/coaches/list";
    }
}

8. Spring Security (10%)
Cerințe minime:
•	Autentificare JDBC
•	Minimum 2 roluri (USER, ADMIN)
•	Protejarea endpoint-urilor bazată pe rol
•	Pagină de login custom
•	Logout funcțional

Cerințe recomandate pentru punctaj maxim:
•	Password encoding (BCrypt)
•	Remember me functionality
•	CSRF protection activă
Criterii evaluare:
•	Autentificare funcțională
•	Autorizare bazată pe roluri
•	Best practices securitate
SecurityConfig:

package com.fitness.opp.config;

import com.fitness.opp.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        
                        .requestMatchers("/users/list/**", "/coaches/list/**", "/workout-groups/list/**", "/nutrition/list/**").authenticated()

                        
                        .requestMatchers("/users/showFormForAdd/**", "/users/save/**", "/users/delete/**", "/users/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        
                        .requestMatchers("/coaches/showFormForAdd/**", "/coaches/save/**", "/coaches/delete/**", "/coaches/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        
                        .requestMatchers("/workout-groups/showFormForAdd/**", "/workout-groups/save/**", "/workout-groups/delete/**", "/workout-groups/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        
                        .requestMatchers("/nutrition/showFormForAdd/**", "/nutrition/save/**", "/nutrition/delete/**", "/nutrition/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/users/list", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("FitnessAppSecretKey_2024_Security")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me")
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
