package org.customtasklist.service;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.DateFilter;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TasklistService {

    @Autowired
    private CamundaTaskListClient camundaTaskListClient;

    public List<Task> getAssignedTasks(String user) throws TaskListException {
        return camundaTaskListClient.getAssigneeTasks(user, TaskState.CREATED, 100).getItems();
    }

    public String completeTask(String taskID, Map<String, Object> variables) throws TaskListException {
        camundaTaskListClient.completeTask(taskID, variables);
        return "Completed at: " + camundaTaskListClient.getTask(taskID).getCompletionDate();
    }

    public List<Task> getAllTasks() throws TaskListException {

        TaskSearch search = new TaskSearch()
                .setState(TaskState.CREATED);

        List<Task> availableTasks = new ArrayList<>(camundaTaskListClient.getTasks(search).getItems());
        return availableTasks;
    }

    public String unclaimTask(String taskID) throws TaskListException {
        camundaTaskListClient.unclaim(taskID);

        return "Unclaimed Task with Task ID: " + taskID;
    }

    public List<Task> getTasksByGroup(String group) throws TaskListException {
        TaskSearch search = new TaskSearch()
                .setState(TaskState.CREATED)
                .setCandidateGroup(group);

        return new ArrayList<>(camundaTaskListClient.getTasks(search).getItems());
    }

    public List<Task> getTaskDueWithin(int days) throws TaskListException {
        LocalDate today = LocalDate.now();
        LocalDate maxDueDate = today.plusDays(days);

        LocalDateTime todayDateTime = today.atStartOfDay();
        LocalDateTime maxDueDateTime = maxDueDate.atStartOfDay();

        DateFilter dueDateFilter = new DateFilter(todayDateTime, maxDueDateTime);

        TaskSearch search = new TaskSearch()
                .setState(TaskState.CREATED)
                .setDueDate(dueDateFilter);

        return camundaTaskListClient.getTasks(search).getItems();
    }


}
