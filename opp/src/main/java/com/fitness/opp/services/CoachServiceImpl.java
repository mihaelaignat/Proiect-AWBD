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