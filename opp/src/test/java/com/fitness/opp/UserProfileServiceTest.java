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
        
        User user = new User();
        user.setId(1L);
        UserProfile profile = new UserProfile();
        profile.setUser(user);

        
        userProfileService.saveProfile(profile);

        
        verify(userProfileRepository, times(1)).save(profile);
    }

    @Test
    public void testSaveProfileException() {
        
        User user = new User();
        user.setId(1L);
        UserProfile profile = new UserProfile();
        profile.setUser(user);

        doThrow(new RuntimeException("DB Error")).when(userProfileRepository).save(any());

        
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
        
        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        
        assertThrows(ResourceNotFoundException.class, () -> {
            userProfileService.getProfileById(99L);
        });
    }
}