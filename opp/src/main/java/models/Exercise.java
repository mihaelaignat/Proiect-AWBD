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

    @NotBlank(message = "Numele exercițiului este obligatoriu")
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