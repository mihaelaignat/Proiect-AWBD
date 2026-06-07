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