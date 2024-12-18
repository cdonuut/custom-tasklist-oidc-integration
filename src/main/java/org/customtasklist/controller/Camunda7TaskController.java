package org.customtasklist.controller;

import org.customtasklist.service.Camunda7TasklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Camunda7TaskController {

    @Autowired
    private Camunda7TasklistService camunda7TasklistService;

    /**
     * Fetches all tasks from Camunda 7's REST API.
     *
     * @return a list of tasks as JSON
     */
    @GetMapping("/camunda7/tasks")
    public ResponseEntity<String> getAllTasksFromCamunda7() {
        return camunda7TasklistService.getAllTasks();
    }

    /**
     * Completes a task in Camunda 7.
     *
     * @param taskId the ID of the task to complete
     * @return Response indicating success or failure
     */
    @PostMapping("/camunda7/complete-task/{taskId}")
    public ResponseEntity<String> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {

        System.out.println("Received Task ID: " + taskId);
        System.out.println("Received Variables: " + variables);

        return camunda7TasklistService.completeTask(taskId, variables);
    }
}