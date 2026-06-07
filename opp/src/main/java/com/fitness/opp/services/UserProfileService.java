package com.fitness.opp.services;
import com.fitness.opp.models.UserProfile;

public interface UserProfileService {
    void saveProfile(UserProfile profile);
    UserProfile getProfileById(Long id);
}