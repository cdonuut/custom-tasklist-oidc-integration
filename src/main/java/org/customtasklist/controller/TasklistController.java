package org.customtasklist.controller;

import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import org.customtasklist.service.TasklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TasklistController {

    @Autowired
    private TasklistService tasklistService;

    /**
     * Retrieves the current authenticated user's attributes if authenticated via OIDC.
     * If the user is not authenticated, returns an error message.
     *
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return a map of the user's attributes if authenticated, otherwise an error message
     */
    @GetMapping("/current-user")
    @ResponseBody
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            return oidcUser.getAttributes();
        }

        return Map.of("Error", "User is not authenticated");
    }

    /**
     * Retrieves the list of tasks assigned to the currently authenticated user.
     * The method fetches the user's preferred username via OIDC authentication and returns tasks from the Tasklist service.
     *
     * @param authentication the {@link Authentication} object containing the user's authentication details
     * @return a list of tasks assigned to the authenticated user
     * @throws TaskListException if an error occurs while fetching tasks from the Tasklist
     */
    @GetMapping("/my-tasks")
    @ResponseBody
    public List<Task> getTasks(Authentication authentication) throws TaskListException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String username = oidcUser.getUserInfo().getPreferredUsername();
        return tasklistService.getAssignedTasks(username);
    }

    /**
     * Completes a specific task identified by the task ID and updates it with the provided variables.
     * This method sends the completion request to the Tasklist service.
     *
     * @param taskID the ID of the task to be completed
     * @param variables a map of variables to update in the task upon completion
     * @return a confirmation message with the completion timestamp
     * @throws TaskListException if an error occurs while completing the task
     */
    @PostMapping("/complete-task/{taskID}")
    @ResponseBody
    public String completeTask(@PathVariable String taskID, @RequestBody Map<String, Object> variables) throws TaskListException {
        return tasklistService.completeTask(taskID, variables);
    }

    /**
     * Retrieves a list of all tasks from the Tasklist service.
     * This method returns all tasks without filtering by assignee or task state.
     *
     * @return a list of all tasks
     * @throws TaskListException if an error occurs while fetching tasks from the Tasklist
     */
    @GetMapping("/all-tasks")
    @ResponseBody
    public List<Task> getTasks() throws TaskListException {
        return tasklistService.getAllTasks();
    }

    /**
     * Unclaims a specific task, making it available for others to claim again.
     * This method sends the unclaim request to the Tasklist service for the specified task.
     *
     * @param taskID the ID of the task to be unclaimed
     * @return a confirmation message indicating the task has been unclaimed
     * @throws TaskListException if an error occurs while unclaiming the task
     */
    @PostMapping("/unclaim-task/{taskID}")
    @ResponseBody
    public String unclaimTask(@PathVariable String taskID) throws TaskListException {
        return tasklistService.unclaimTask(taskID);
    }

    /**
     * Retrieves tasks that belong to a specific group.
     * This method fetches tasks from the Tasklist service that are associated with the specified group.
     *
     * @param group the name or ID of the group to filter tasks by
     * @return a list of tasks assigned to the specified group
     * @throws TaskListException if an error occurs while fetching tasks from the Tasklist
     */
    @PostMapping("/group/{group}")
    @ResponseBody
    public List<Task> groupTask(@PathVariable String group) throws TaskListException {
        return tasklistService.getTasksByGroup(group);
    }

    @GetMapping("/tasks/due-in/{days}")
    @ResponseBody
    public List<Task> getTasksDueWithin(@PathVariable Integer days) throws TaskListException {
        return tasklistService.getTaskDueWithin(days);
    }


}
