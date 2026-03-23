package com.example.Task_Manager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.Task_Manager.model.Task;
import com.example.Task_Manager.model.User;
import com.example.Task_Manager.Repository.TaskRepository;
import com.example.Task_Manager.Repository.UserRepository;
import com.example.Task_Manager.TokenUtil;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenUtil tokenUtil;

    @PostMapping("/add")
    public String createTask(
            @RequestBody Task task,
            @RequestHeader("Authorization") String token) {

        String username = tokenUtil.validateToken(token);

        if (username == null) {
            return "Unauthorized";
        }

        task.setUsername( username);
        taskRepo.save(task);

        return "Task Added Successfully";
    }

    @GetMapping("/all")
    public Object getTasks(
            @RequestHeader("Authorization") String token) {

        String username = tokenUtil.validateToken(token);

        if (username == null) {
            return "Unauthorized";
        }

        return taskRepo.findByUsername(username);
    }

    @GetMapping("/admin")
    public Object adminTasks(
            @RequestHeader("Authorization") String token) {

        String username = tokenUtil.validateToken(token);

        if (username == null) {
            return "Unauthorized";
        }

        User user = userRepo.findByUsername(username);

        if (user == null) {
            return "Unauthorized";
        }

        if (!user.getrole().equals("ADMIN")) {
            return "Access Denied";
        }

        return taskRepo.findAll();
    }
    @PutMapping("/{id}")
    public Object updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask,
            @RequestHeader("Authorization") String token) {

        String username = tokenUtil.validateToken(token);

        if (username == null) {
            return "Unauthorized";
        }

        Task existingTask = taskRepo.findById(id).orElse(null);

        if (existingTask == null) {
            return "Task not found";
        }

        if (!existingTask.getUsername().equals(username)) {
            return "Access Denied";
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setdescription(updatedTask.getdescription());

        taskRepo.save(existingTask);

        return "Task Updated Successfully";
    }
}