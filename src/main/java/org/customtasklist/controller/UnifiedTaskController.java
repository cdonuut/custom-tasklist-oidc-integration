package org.customtasklist.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.tasklist.dto.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UnifiedTaskController {

    @Autowired
    private Camunda7TaskController camunda7TaskController;

    @Autowired
    private TasklistController camunda8TaskController;

    @GetMapping("/unified-tasks")
    public ResponseEntity<Object> getUnifiedTasks() {
        try {
            ResponseEntity<String> camunda7Response = camunda7TaskController.getAllTasksFromCamunda7();
            List<Map<String, Object>> camunda7Tasks = new ObjectMapper()
                    .readValue(camunda7Response.getBody(), new TypeReference<>() {});

            List<Task> camunda8Tasks = camunda8TaskController.getTasks();
            List<Map<String, Object>> camunda8TaskMaps = camunda8Tasks.stream()
                    .map(task -> Map.<String, Object>of(
                            "id", task.getId(),
                            "name", task.getName(),
                            "source", "Camunda 8"
                    ))
                    .collect(Collectors.toList());

            camunda7Tasks.forEach(task -> task.put("source", "Camunda 7"));

            List<Map<String, Object>> unifiedTasks = new ArrayList<>();
            unifiedTasks.addAll(camunda7Tasks);
            unifiedTasks.addAll(camunda8TaskMaps);

            if (unifiedTasks.isEmpty()) {
                String message = "No open tasks on Camunda 7 and 8. Consider running a process or wait for tasks to be available and come back later.";
                return ResponseEntity.ok(Map.of("message", message));
            }

            return ResponseEntity.ok(unifiedTasks);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred while fetching tasks: " + e.getMessage()));
        }
    }

    @PostMapping("/complete-task")
    public ResponseEntity<String> completeTask(@RequestBody Map<String, Object> payload) {
        String taskId = (String) payload.get("taskId");

        // Determine task source
        if (isUUID(taskId)) {
            return completeCamunda7Task(taskId, payload);
        } else if (isLong(taskId)) {
            return completeCamunda8Task(taskId, payload);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid task ID format: " + taskId);
        }
    }

    // Utility: Check if the ID is a UUID (Camunda 7)
    private boolean isUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Utility: Check if the ID is a Long (Camunda 8)
    private boolean isLong(String id) {
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ResponseEntity<String> completeCamunda7Task(String taskId, Map<String, Object> payload) {
        try {
            Map<String, Object> variables = (Map<String, Object>) payload.get("variables");
            Map<String, Object> transformedVariables = new HashMap<>();

            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    transformedVariables.put(entry.getKey(), Map.of(
                            "value", entry.getValue(),
                            "type", "String"
                    ));
                }
            }

            Map<String, Object> requestBody = Map.of("variables", transformedVariables);

            return camunda7TaskController.completeTask(taskId, requestBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error completing task in Camunda 7: " + e.getMessage());
        }
    }

    private ResponseEntity<String> completeCamunda8Task(String taskId, Map<String, Object> variables) {
        try {
            String completionMessage = camunda8TaskController.completeTask(taskId, variables);

            return ResponseEntity.ok(completionMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error completing task in Camunda 8: " + e.getMessage());
        }
    }
}