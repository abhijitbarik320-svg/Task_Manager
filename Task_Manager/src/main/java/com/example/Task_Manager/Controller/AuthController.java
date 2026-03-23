package com.example.Task_Manager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Task_Manager.Repository.UserRepository;
import com.example.Task_Manager.Repository.TaskRepository;
import com.example.Task_Manager.TokenUtil;
import com.example.Task_Manager.model.User;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TaskRepository taskRepo; 

    @Autowired
    private TokenUtil tokenUtil;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        repo.save(user);
        return "User Registered Successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {

        User dbUser = repo.findByUsername(user.getUsername());

        if (dbUser != null &&
            dbUser.getPassword().equals(user.getPassword())) {

            return tokenUtil.generateToken(user.getUsername());
        }

        return "Invalid Username or Password";
    }

    @DeleteMapping("/{username}")
    public String deleteUser(@PathVariable String username) {

        User user = repo.findByUsername(username);

        if (user == null) {
            return "User not found";
        }

        taskRepo.deleteAll(taskRepo.findByUsername(username));

        repo.delete(user);

        return "User and all tasks deleted successfully";
    }
}