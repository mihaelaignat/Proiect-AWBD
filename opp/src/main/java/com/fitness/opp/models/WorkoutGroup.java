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