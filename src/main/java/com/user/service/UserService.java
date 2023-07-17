package com.user.service;

import com.user.entities.User;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    //create user
    User saveUser(User user);

    //get all users
    List<User> getUsers();

    //get user by id
    User getUserById(String id);
}
