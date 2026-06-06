erDiagram
    USER ||--|| USER_PROFILE : "has"
    USER }o--|| COACH : "assigned_to"
    USER }o--|| NUTRITION_PLAN : "follows"
    WORKOUT_GROUP }o--o{ EXERCISE : "workout_group_exercises"

    USER {
        long id PK
        string username
        string password
        string email
        string role
        long nutrition_plan_id FK
        long coach_id FK
    }

    USER_PROFILE {
        long id PK
        long user_id FK
        int age
        double weight
        string fitnessGoal
    }

    COACH {
        long id PK
        string name
        string specialization
    }

    NUTRITION_PLAN {
        long id PK
        string name
        string description
        int calories
    }

    WORKOUT_GROUP {
        long id PK
        string name
        string description
        string category
    }

    EXERCISE {
        long id PK
        string name
        string description
        string muscleGroup
        int sets
        int reps
        int duration
    }