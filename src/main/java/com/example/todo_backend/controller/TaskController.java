package com.example.todo_backend.controller;

import com.example.todo_backend.entity.Task;
import com.example.todo_backend.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<Task> getAllTasks()
    {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task)
    {
        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTask(@PathVariable Long id)
    {
        taskRepository.deleteById(id);

        Map<String, Object> res = new HashMap<>();
        res.put("Id",id);
        res.put("DELETE","SUCCESS");
        return res;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask) {

        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setStatus(updatedTask.isStatus());

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("data", taskRepository.save(existingTask));

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("status", "ERROR");
                    error.put("message", "Task not found");
                    error.put("id", id);

                    return ResponseEntity.status(404).body(error);
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {

        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            return ResponseEntity.ok(optionalTask.get());
        }

        return ResponseEntity.status(404)
                .body("Task not found with id: " + id);
    }
}
