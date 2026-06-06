package com.fitness.opp.services;

import com.fitness.opp.models.User;
import org.springframework.data.domain.Page;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long theId);
    void save(User theUser);
    void deleteById(Long theId);
    Page<User> findPaginated(int pageNo, int pageSize, String sortField);
}