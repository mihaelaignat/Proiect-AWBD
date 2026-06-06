package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.UserProfile;
import com.fitness.opp.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j; // IMPORT IMPORTANT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j // Adaugă automat obiectul 'log'
@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public void saveProfile(UserProfile profile) {
        log.info("Încercare salvare profil pentru utilizatorul ID: {}", profile.getUser().getId());
        try {
            userProfileRepository.save(profile);
            log.debug("Profil salvat cu succes în baza de date.");
        } catch (Exception e) {
            log.error("Eroare critică la salvarea profilului: {}", e.getMessage());
        }
    }

    @Override
    public UserProfile getProfileById(Long id) {
        log.info("Căutare profil cu ID-ul: {}", id);
        return userProfileRepository.findById(id).orElseThrow(() -> {
            log.error("Resursa solicitata (Profil ID: {}) nu a fost gasita!", id);
            return new ResourceNotFoundException("Profil negasit");
        });
    }
}