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