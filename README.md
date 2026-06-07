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
    private List<com.fitness.opp.models.User> users;
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
    private List<com.fitness.opp.models.User> users;
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
    private com.fitness.opp.models.UserProfile profile;
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
	private List<com.fitness.opp.models.User> members = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "workout_group_exercises",
            joinColumns = @JoinColumn(name = "workout_group_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private List<com.fitness.opp.models.Exercise> exercises = new ArrayList<>();

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

Update: add-coach.html

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

Delete: butonul “Delete” coaches-list.html

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

package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.Coach;
import com.fitness.opp.models.User;
import com.fitness.opp.repository.CoachRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

    private Coach sampleCoach;

    @BeforeEach
    void setUp() {
        sampleCoach = new Coach();
        sampleCoach.setId(1L);
        sampleCoach.setName("Popescu Ion");
        sampleCoach.setUsers(new ArrayList<>());
    }

    @Test
    void testGetAllCoaches() {
        List<Coach> coaches = List.of(sampleCoach);
        when(coachRepository.findAll()).thenReturn(coaches);

        List<Coach> result = coachService.getAllCoaches();

        assertEquals(1, result.size());
        verify(coachRepository, times(1)).findAll();
    }

    @Test
    void testSaveCoach() {
        coachService.saveCoach(sampleCoach);
        verify(coachRepository, times(1)).save(sampleCoach);
    }

    @Test
    void testGetCoachById_Success() {
        when(coachRepository.findById(1L)).thenReturn(Optional.of(sampleCoach));

        Coach result = coachService.getCoachById(1L);

        assertNotNull(result);
        assertEquals("Popescu Ion", result.getName());
    }

    @Test
    void testGetCoachById_ThrowsException() {
        when(coachRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> coachService.getCoachById(1L));
    }

    @Test
    void testDeleteCoach_Success() {
        when(coachRepository.findById(1L)).thenReturn(Optional.of(sampleCoach));

        coachService.deleteCoach(1L);

        verify(coachRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCoach_ThrowsBusinessError() {
        List<User> clients = new ArrayList<>();
        clients.add(new User());
        sampleCoach.setUsers(clients);

        when(coachRepository.findById(1L)).thenReturn(Optional.of(sampleCoach));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> coachService.deleteCoach(1L));
        assertTrue(exception.getMessage().contains("Nu se poate sterge un antrenor care are clienti"));

        verify(coachRepository, never()).deleteById(anyLong());
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

	@ManyToOne
	@JoinColumn(name = "workout_group_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private WorkoutGroup workoutGroup;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private com.fitness.opp.models.UserProfile profile;
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
            <th>Antrenor</th>
            <th sec:authorize="hasAuthority('ADMIN')">Actiuni</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="tempUser : ${users}">
            <td th:text="${tempUser.username}" class="fw-bold align-middle"></td>
            <td th:text="${tempUser.email}" class="align-middle"></td>
            <td class="align-middle"><span class="badge bg-secondary" th:text="${tempUser.role}"></span></td>
            <td th:text="${tempUser.coach != null} ? ${tempUser.coach.name} : 'Fara Antrenor'" class="align-middle text-muted"></td>
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
                                <div class="mb-3">
                                    <label class="form-label">Antrenor Asociat</label>
                                    <select name="coachId" class="form-select">
                                        <option th:value="-1">--- Fara Antrenor ---</option>
                                        <option th:each="tempCoach : ${coaches}"
                                                th:value="${tempCoach.id}"
                                                th:text="${tempCoach.name}"
                                                th:selected="${tempUser.coach != null && tempUser.coach.id == tempCoach.id}">
                                        </option>
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
            <li class="page-item" th:classappend="${currentPage == totalPages - 1} ? 'disabled'"><a class="page-link" th:href="@{/users/list(page=${currentPage + 1}, sort=${sortField})}">Inainte</a></li>
        </ul>
    </nav>
</div>

<div class="modal fade" id="addUserModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog text-start"><div class="modal-content">
        <div class="modal-header bg-success text-white"><h5 class="modal-title">Adauga Utilizator Nou</h5><button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button></div>
        <form th:action="@{/users/save}" th:object="${newUser}" method="POST">
            <div class="modal-body">
                <div class="mb-3"><label class="form-label">Username</label><input type="text" th:field="*{username}" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Email</label><input type="email" th:field="*{email}" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Parola</label><input type="password" th:field="*{password}" class="form-control" required></div>
                <div class="mb-3">
                    <label class="form-label">Rol</label>
                    <select th:field="*{role}" class="form-select">
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                        <option value="COACH">COACH</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Antrenor Asociat</label>
                    <select name="coachId" class="form-select">
                        <option th:value="-1">--- Fara Antrenor ---</option>
                        <option th:each="tempCoach : ${coaches}"
                                th:value="${tempCoach.id}"
                                th:text="${tempCoach.name}">
                        </option>
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

Descrierea proiectului:

Introducere si Obiective

Proiectul FitCore reprezinta o platforma informatica de tip Enterprise, dezvoltata utilizand framework-ul Spring Boot, destinata managementului integrat al activitatilor dintr-o retea de sali de fitness. 
Obiectivul principal al aplicatiei este digitalizarea interactiunii dintre administratori, antrenori si clienti, oferind instrumente avansate pentru gestionarea membrilor, planificarea antrenamentelor si monitorizarea planurilor nutritionale.
Platforma este conceputa sa ruleze intr-un mediu securizat, oferind o interfata web moderna, dinamica si complet optimizata pentru fluxuri de lucru intense (CRUD, sortare si paginare pe seturi mari de date).

Arhitectura Tehnica si Tehnologii

Aplicatia respecta bunele practici de inginerie software, fiind structurata pe straturi clare de responsabilitate (Layered Architecture):

•	Stratul de Prezentare (Frontend): Dezvoltat in Thymeleaf si HTML5, asigurand o interfata receptiva (responsive) si utilizarea componentelor moderne precum ferestrele modale pentru operatiuni rapide.
•	Stratul de Control (Controller): Mapat cu Spring pentru gestionarea cererilor HTTP si transmiterea datelor.
•	Stratul de Business (Service): Implementeaza regulile stricte de afaceri ale aplicatiei.
•	Stratul de Persistenta (Data Access): Realizat cu Spring Data JPA si Hibernate, conectat la un sistem de gestiune a bazelor de date relationale (Oracle Server).
•	Securitate: Gestionata integral prin Spring Security, cu criptare de tip BCrypt pentru parole si autorizare bazata pe roluri stricte.

3. Entitati Principale si Modelul Conceptual
Baza de date a sistemului este normalizata si construita in jurul urmatoarelor module interconectate:
•	Utilizatori (Users): Membrii clubului de fitness. Fiecare utilizator are asociat un rol generic (USER, ADMIN, COACH) si poate fi alocat unui antrenor specific.
•	Profil Utilizator (User Profiles): Salveaza datele utilizatorilor si obiectivele specifice ale membrului (greutate, varsta, obiectiv), asigurand o separare curata a datelor personale de cele de autentificare.
•	Antrenori (Coaches): Personalul specializat care coordoneaza activitatea sportiva.
•	Planuri Nutritionale (Nutrition Plans): Programe alimentare definite prin nume, numar de calorii si recomandari specifice.
•	Grupuri de Antrenament (Workout Groups): Clase sau sesiuni organizate pe categorii.
•	Exercitii (Exercises): Catalogul de miscari si tehnici utilizate in antrenamente.

Setup instructions:

1. Instalare Oracle Enterprise Edition
2. Instalare IntelliJ IDEA 2025.3.3
3. In bara de “Search” -> “ Services.msc”
4. Pornim OracleVssWriterXE, OracleServiceXE, OracleOraDB21Home2TNSListener, OracleOraDB21Home2MTSRecoveryService
5. Deschidem IntelliJ IDEA 2025.3.3 -> Open -> C:\Users\Dunduc George\Desktop\fitness
6. Deschidem fitness -> opp -> src -> main -> java -> com.fitness.opp -> OppApplication
7. Click dreapta -> Run (OppApplication.main)
8. Accesam http://localhost:8080/login
9. Folosim pentru login: user: alexandra_fit / mihaela_fit / george_fit ; parola: 1234



Documentatia API (API Documentation)

In cadrul arhitecturii MVC a platformei, comunicarea dintre interfata grafica si stratul de backend se realizeaza prin intermediul unor endpoint-uri HTTP bine definite. 

Aceste rute gestioneaza operatiunile de tip CRUD (Create, Read, Update, Delete) si asigura transmiterea securizata a datelor.

Toate rutele expuse de aplicatie sunt protejate prin Spring Security, accesul la metodele de modificare (POST/DELETE) fiind conditionat de detinerea rolului de ADMIN.

1. Modul Management Utilizatori (/users)
1.1. Vizualizare lista paginata si sortata
•	URL: http://localhost:8080/users/list
•	Metoda HTTP: GET
•	Parametri Request (Query Params):
o	page (int, optional, default: 0): Numarul paginii curente.
o	sort (String, optional, default: username): Campul dupa care se face sortarea datelor.
•	Descriere: Returneaza pagina HTML users-list continand colectia paginata de utilizatori extrasa din baza de date Oracle, datele de paginare si un obiect gol de tip User pentru formularul de adaugare.

1.2. Salvare sau Actualizare Utilizator

•	URL: /users/save
•	Metoda HTTP: POST
•	Corp Cerere (Form Data):
o	id (Long, optional): ID-ul utilizatorului (daca este prezent, se efectueaza Update; daca este null sau 0, se efectueaza Create).
o	username (String, required): Numele de utilizator unic.
o	email (String, required): Adresa de email.
o	password (String, conditional): Parola contului (obligatorie doar la crearea unui cont nou).
o	role (String, required): Rolul alocat (USER, ADMIN, COACH).
o	coachId (Long, optional): ID-ul antrenorului alocat (-1 reprezinta lipsa unui antrenor).
•	Descriere: Proceseaza datele trimise din formular. Daca utilizatorul este nou, parola este criptata folosind algoritmul BCrypt. Daca este transmisa o valoare valida pentru coachId, sistemul cauta antrenorul si realizeaza maparea. Dupa salvare, se efectueaza o redirectionare catre /users/list.

1.3. Stergere Utilizator
•	URL: /users/delete
•	Metoda HTTP: GET
•	Parametri Request (Query Params):
o	userId (Long, required): ID-ul unic al utilizatorului ce urmeaza a fi eliminat.
•	Descriere: Sterge inregistrarea utilizatorului din baza de date pe baza ID-ului furnizat si redirectioneaza catre lista actualizata.

2. Modul Planuri Nutritionale (/nutrition)
2.1. Vizualizare lista planuri
•	URL: http://localhost:8080/nutrition/list
•	Metoda HTTP: GET
•	Parametri Request (Query Params):
o	page (int): Pagina curenta.
o	sortField (String): Proprietatea selectata pentru sortare (name, calories).
o	sortDir (String): Directia de sortare (asc sau desc).
•	Descriere: Incarca pagina nutrition-list cu toate planurile nutritionale disponibile, inclusiv numele, caloriile si descrierea/observatiile aferente.

2.2. Salvare Plan Nutritional
•	URL: /nutrition/save
•	Metoda HTTP: POST
•	Corp Cerere (Form Data):
o	id (Long, optional): ID-ul planului.
o	name (String, required): Denumirea planului (ex: Bulk, Low Carb).
o	calories (int, required): Numarul total de calorii.
o	description (String, optional): Detalii text sau observatii privind alimentele recomandate.
•	Descriere: Persista sau actualizeaza planul nutritional in tabela corespunzatoare din baza de date Oracle, apoi redirectioneaza utilizatorul catre /nutrition/list.

2.3. Stergere Plan Nutritional
•	URL: /nutrition/delete
•	Metoda HTTP: GET
•	Parametri Request (Query Params):
o	planId (Long, required): ID-ul planului ce trebuie sters.
•	Descriere: Elimina planul nutritional selectat din sistem.

3. Modul Antrenori (/coaches)
3.1. Stergere Antrenor (Cu Validare de Business)
•	URL: /coaches/delete
•	Metoda HTTP: GET
•	Parametri Request (Query Params):
o	coachId (Long, required): ID-ul antrenorului curent.
•	Descriere: Inainte de stergere, stratul de business interogheaza relatia cu tabela USERS. Daca lista de clienti asociati antrenorului nu este goala, operatiunea este abandonata si se arunca o exceptie de tipul RuntimeException pentru a proteja integritatea datelor si a preveni existenta inregistrarilor orfane.


Arhitectura Aplicatiei (Application Architecture)
Design-ul arhitectural al platformei software este construit pe principiul separarii responsabilitatilor (Separation of Concerns), utilizand o Arhitectura Stratificata (Layered Architecture) pe 4 niveluri principale, combinata cu sablonul de proiectare MVC (Model-View-Controller).
Aceasta structura garanteaza o mentenabilitate ridicata, decuplarea componentelor si permite testarea izolata a fiecarei componente logice.

1. Diagrama Conceptuala a Straturilor (Layers)
Fluxul de date in cadrul aplicatiei se desfasoara unidirectional, de sus in jos, prin urmatoarele straturi:
1.	Stratul de Prezentare (View / Frontend): Realizat in Thymeleaf si HTML, responsabil doar cu afisarea datelor si capturarea actiunilor utilizatorului (evenimente de click, trimiteri de formulare).
2.	Stratul de Control (Controller / Routing): Intercepteaza cererile HTTP venite din browser (GET/POST), valideaza parametrii de intrare, apeleaza logica de business si returneaza vederea (View-ul) corespunzatoare.
3.	Stratul de Business (Service Layer): Contine regulile stricte de functionare ale aplicatiei (coordonarea operatiunilor, validari complexe, aruncarea excepțiilor de business - cum este blocarea stergerii unui antrenor cu clienti).
4.	Stratul de Persistenta (Data Access Layer / Repository): interactioneaza direct cu baza de date relationala Oracle prin intermediul Spring Data JPA si Hibernate.

2. Sablonul Model-View-Controller (MVC) in Contextul Spring Boot
Aplicatia utilizeaza arhitectura MVC pentru a gestiona interactiunile dinamic:
•	Model: Reprezentat de clasele de tip Entitate (User, Coach, NutritionPlan, Exercise, UserProfile, WorkoutGroup). Acestea reflecta structura tabelelor din baza de date Oracle si pastreaza starea datelor.
•	View: Reprezentat de paginile HTML procesate de motorul de sabloane Thymeleaf (users-list.html, nutrition-list.html). Acestea sunt dinamice, randand datele injectate de controller.
•	Controller: Reprezentat de clasele adnotate cu @Controller (UserViewController, CoachViewController). Rolul lor este de a face legatura intre Model si View.

3. Securitatea Arhitecturala (Spring Security Layer)
Un aspect critic al arhitecturii este interceptarea cererilor prin Spring Security. Acesta actioneaza ca un strat de tip filtru (Filter Chain) inainte ca cererea HTTP sa ajunga la Controller:
•	Autentificare: Verifica identitatea utilizatorului pe baza credentialelor stocate in baza de date. Parolele nu sunt salvate in clar, ci sunt procesate prin filtrul de criptare BCrypt Password Encoder.
•	Autorizare (RBAC): Restrictionarea accesului la nivel de URL. De exemplu, rutele de tip /users/delete sau /nutrition/save sunt blocate la nivel architectural daca utilizatorul autentificat nu detine autoritatea ADMIN.

4. Fluxul unei Cereri HTTP (Exemplu Practic)
Pentru a exemplifica functionarea arhitecturii, parcursul unei actiuni de salvare a unui utilizator (/users/save) urmeaza pasii:
1.	Client (Browser): Utilizatorul completeaza formularul in fereastra modala din users-list.html si apasa "Salveaza". Browserul trimite o cerere POST catre /users/save.
2.	Securitate: Spring Security intercepteaza cererea si verifica daca utilizatorul are rolul de ADMIN. Daca da, permite trecerea mai departe.
3.	Controller: UserViewController receptioneaza datele din formular prin @ModelAttribute si ID-ul antrenorului prin @RequestParam.
4.	Service: Controllerul apeleaza userService.save(theUser). In acest strat se aplica logica de criptare a parolei (daca userul este nou) si se injecteaza obiectul Coach extras din coachService.
5.	Repository: UserRepository (interfata ce extinde JpaRepository) apeleaza metoda .save(). Hibernate traduce aceasta actiune intr-o comanda SQL de tip INSERT sau UPDATE si o executa pe baza de date Oracle.
6.	Raspuns: Controllerul primeste confirmarea succesului si trimite inapoi un raspuns de tip redirect:/users/list, fortand browserul sa reincarce lista actualizata.


Contributii Membrii Echipei

Colaborarea in cadrul echipei a fost realizata echitabil, sarcinile fiind impartite in mod egal după cum urmeaza:

* Dunduc George:
    * Configurarea initiala a proiectului Spring Boot si managementul dependentelor (Maven tooling).
    * Implementarea stratului de Business Logic (Service-uri) și a claselor de baza.
    * Gestionarea mecanismelor de logging ale sistemului si structurarea arhitecturii directoarelor.

* Mihaela Ignat:
    * Proiectarea schemei bazei de date si maparea entitatilor din sistem.
    * Implementarea Controller-elor REST si expunerea endpoint-urilor API pentru utilizatori.
    * Realizarea documentatiei tehnice a proiectului si design-ul diagramei conceptuale (`diagrama.JPG`).

* Alexandra Putanu:
    * Implementarea stratului de validare a datelor de intrare (Validation Layer) pentru securizarea API-ului.
    * Scrierea testelor unitare si de integrare pentru asigurarea calitatii componentelor software.
    * Analiza și corectarea erorilor apărute in faza de testare locala a endpoint-urilor.





