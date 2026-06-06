package com.fitness.opp.services;

import com.fitness.opp.exceptions.ResourceNotFoundException;
import com.fitness.opp.models.User;
import com.fitness.opp.models.UserProfile;
import com.fitness.opp.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Test
    public void testSaveProfileSuccess() {
        // Pregătim datele
        User user = new User();
        user.setId(1L);
        UserProfile profile = new UserProfile();
        profile.setUser(user);

        // Executăm metoda (Asta va colora log.info și repository.save în VERDE)
        userProfileService.saveProfile(profile);

        // Verificăm execuția
        verify(userProfileRepository, times(1)).save(profile);
    }

    @Test
    public void testSaveProfileException() {
        // Simulăm o eroare pentru a intra în blocul CATCH (Asta va colora log.error în VERDE)
        User user = new User();
        user.setId(1L);
        UserProfile profile = new UserProfile();
        profile.setUser(user);

        doThrow(new RuntimeException("DB Error")).when(userProfileRepository).save(any());

        // Apelăm metoda - catch-ul va gestiona eroarea conform codului tău
        userProfileService.saveProfile(profile);

        verify(userProfileRepository, times(1)).save(any());
    }

    @Test
    public void testGetProfileByIdSuccess() {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        UserProfile result = userProfileService.getProfileById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetProfileByIdNotFound() {
        // Simulăm cazul în care profilul nu există pentru a intra în orElseThrow
        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        // Verificăm că aruncă excepția custom (Asta va colora log.error de la final în VERDE)
        assertThrows(ResourceNotFoundException.class, () -> {
            userProfileService.getProfileById(99L);
        });
    }
}